package me.flyray.bsin.domain.request;

import lombok.Data;

import java.util.List;

@Data
public class CreateMpcSignRequest {
    private String customerRefId;
    private String sourceAccountKey;
    private String signAlg;
    private List<Hash> hashs;

   public static class Hash{
        private String hash;
        private String note;

       public String getHash() {
           return hash;
       }

       public void setHash(String hash) {
           this.hash = hash;
       }

       public String getNote() {
           return note;
       }

       public void setNote(String note) {
           this.note = note;
       }
   }




}
