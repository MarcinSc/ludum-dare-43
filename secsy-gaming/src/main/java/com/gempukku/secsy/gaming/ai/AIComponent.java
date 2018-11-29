package com.gempukku.secsy.gaming.ai;

import com.gempukku.secsy.entity.Component;

import java.util.Map;

public interface AIComponent extends Component {
    String getAiName();

    Map<String, Object> getValues();

    void setValues(Map<String, Object> values);
}
