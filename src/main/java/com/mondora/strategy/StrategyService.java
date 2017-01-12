package com.mondora.strategy;

import com.mondora.model.Event;
import com.mondora.strategy.ExecuteStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * |__________________________________________________________
 * |   _                      __                              |
 * |  (_) __ _  ___  ___  ___/ /__  _______ _ _______  __ _   |
 * |     /  ' \/ _ \/ _ \/ _  / _ \/ __/ _ `// __/ _ \/  ' \  |
 * |(_) /_/_/_/\___/_//_/\_,_/\___/_/  \_,_(_)__/\___/_/_/_/  |
 * |                 - computing essence -                    |
 * |__________________________________________________________|
 * |
 * Created by atibi on 22/06/16.
 */
@Component
public class StrategyService {
    private final Map<String, ExecuteStrategy> updateStrategyMap;

    @Autowired
    public StrategyService() throws Exception {
        this.updateStrategyMap = new HashMap<>();
//        this.updateStrategyMap.put("ROLES_UPDATED", new UpdateScopes(userAuthScopeRepository));
        this.updateStrategyMap.put( "*", new LogMessage() );
    }

    public void execute(Event event) throws Exception {
        String eventName = Optional.ofNullable(event.getEventName()).orElse("");
        Optional.ofNullable(updateStrategyMap.get("*")).ifPresent(value -> value.execute(event));
        if (event.getId() != null) {
            Optional.ofNullable(updateStrategyMap.get(eventName)).ifPresent(value -> value.execute(event));
        }
    }
}