#!groovy​
node {
    def DEPLOY_REPOS = [
            ['fb-bot', 'git@bitbucket.org:mondora/fb-bot.git'],
    ]
    def TOOLS = ["Maven 3", "Jdk_8u102"]

    try {
        stage("Checkout") {
            checkout scm
        }

        def flavor = env.BRANCH_NAME
        echo "Building flavor: ${flavor}"
        def shortCommit = sh(returnStdout: true, script: "git rev-parse --short HEAD").trim()
        def commitAuthor = sh(returnStdout: true, script: "git --no-pager show -s --format='%an' HEAD").trim()
        def commitMessage = "deploy ${shortCommit}"

        stage("Set up environment") {
            for (String t : TOOLS) {
                def toolPath = tool(t);
                env.PATH = "${toolPath}/bin:${env.PATH}"
            }
        }

        stage("Build and Test") {
            sh "mvn --batch-mode -V -U -e clean package -Dsurefire.useFile=false"
        }

        for (List deployRepo : DEPLOY_REPOS) {
            def artifactName = deployRepo[0]
            def targetDir = artifactName?.trim() ? artifactName + "/target" : "target"
            def deployRepoDir = targetDir + "/" + shortCommit
            def deployScm = deployRepo[1]

            stage("Stash artifact") {
                dir(targetDir) {
                    stash includes: '*.jar', name: 'fatjar'
                }
            }

            stage("Copy artifact") {
                dir(deployRepoDir) {
                    checkout changelog: false, poll: false, scm: [$class: 'GitSCM', branches: [[name: "*/${flavor}"]], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'CloneOption', depth: 0, noTags: true, reference: '', shallow: true]], submoduleCfg: [], userRemoteConfigs: [[credentialsId: 'b2b-deploy', url: deployScm]]]
                    sh "find *.jar -delete || true"
                    unstash 'fatjar'
                }
            }

            stage("Commit and Push") {
                dir(deployRepoDir) {
                    sh "git add *.jar --all"
                    sh "if ! git diff-index --quiet HEAD --; then git commit -m '${commitMessage}'; fi"
                    sh "git push origin HEAD:${flavor}"
                }
            }

            stage('Notify') {
                slackSend color: 'good', message: "B2B - ${artifactName}/${flavor} (${shortCommit}) - ${BUILD_DISPLAY_NAME} Success (<${BUILD_URL}|Open>) - Triggered by ${commitAuthor}"
            }
        }
    } catch (err) {
        slackSend color: 'danger', message: "B2B - ${JOB_NAME} - ${BUILD_DISPLAY_NAME} Failure (<${BUILD_URL}|Open>) - Caught: ${err}"
        currentBuild.result = 'FAILURE'
    }
}
