package me.flyray.bsin.blockchain.utils;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class buildTest {


    @Test
    public void java2ContractTypeParameterBuildTest() {
        Java2ContractTypeParameter java2ContractTypeParameter1 = new Java2ContractTypeParameter.Builder().addValue("address", List.of("0x01", "0x02")).addParameter().build();
        Java2ContractTypeParameter java2ContractTypeParameter2 = new Java2ContractTypeParameter.Builder().addValue("address", List.of("0x01", "0x02")).addParameter().addValue("uint256", List.of("256")).addParameter().build();

        System.out.println("java2ContractTypeParameter1: " + java2ContractTypeParameter1.toString());
        System.out.println("java2ContractTypeParameter1: " + java2ContractTypeParameter1.toString());
    }
}