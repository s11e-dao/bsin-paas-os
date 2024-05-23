package me.flyray.bsin.domain.entity;

import org.flowable.engine.form.FormType;

public interface FormProperty {

        String getId();

        /** 显示标签 */
        String getName();


        FormType getType();

        /** 可选。这个参数需要显示的值 */
        String getValue();

        boolean isReadable();

        /** 用户提交表单时是否可以包含这个参数？ */
        boolean isWritable();

        /** 输入框中是否必填这个参数 */
        boolean isRequired();


}
