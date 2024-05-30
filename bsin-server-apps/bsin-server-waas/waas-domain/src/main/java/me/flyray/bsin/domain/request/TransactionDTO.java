package me.flyray.bsin.domain.request;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import me.flyray.bsin.domain.entity.Transaction;
import org.springframework.format.annotation.DateTimeFormat;

@Data
public class TransactionDTO extends Transaction {
    public Integer current;

    public Integer size;

    /**
     * 发送/接受地址
     */
    public String address;

    /**
     * 币种
     */
    public String coin;

    /**
     * 主链
     */
    public String chainName;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public String startTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public String  endTime;
}
