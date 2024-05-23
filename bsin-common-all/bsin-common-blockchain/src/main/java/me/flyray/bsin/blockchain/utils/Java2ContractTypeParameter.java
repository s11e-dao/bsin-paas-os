package me.flyray.bsin.blockchain.utils;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class Java2ContractTypeParameter {
    private List<String> value;     //具体变量参数，可以是list，默认只有一个值
    private ParameterStruct parameter;      // (type,List<String>) --> type: value
    private List<ParameterStruct> parameterList;    // List<parameter>

    private Java2ContractTypeParameter(Builder builder) {
        this.value = builder.value;
        this.parameter = builder.parameter;
        this.parameterList = builder.parameterList;
    }


    @Data
    public static class ParameterStruct {
        private String type;
        private List<String> parameter;

        public ParameterStruct() {
            type = "";
            parameter = new ArrayList<String>();
        }
    }

    public static class Builder {
        private List<String> value;
        private ParameterStruct parameter;
        private List<ParameterStruct> parameterList;

        public Builder() {
            this.value = new ArrayList<String>();
            this.parameter = new ParameterStruct();
            this.parameterList =
                    new ArrayList<ParameterStruct>();
        }

        public Builder addValue(String type, List<String> value) {
            List tmp = new ArrayList<String>();
            if (value != null) {
                tmp.addAll(value);
            }else{
                tmp.add("");
            }
            this.parameter.parameter = tmp;
            this.parameter.type = type;
            return this;
        }

        public Builder addParameter() {
            ParameterStruct param = new ParameterStruct();
            param.parameter.addAll(this.parameter.getParameter());
            param.setType(this.parameter.getType());
            this.parameterList.add(param);
            this.parameter.parameter.clear();
            return this;
        }

        public Java2ContractTypeParameter build() {
            return new Java2ContractTypeParameter(this);
        }
    }
}