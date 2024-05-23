package me.flyray.bsin.utils;

import lombok.Data;

/**
 * @author bolei
 * @date 2024/1/3
 * @desc
 */

@Data
public class I18eCode {

    public I18eCode(String i18eCode){
        this.i18eCode = i18eCode;
    }

    String i18eCode;
}
