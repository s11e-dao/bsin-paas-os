package me.flyray.bsin.blockchain.utils;

import org.springframework.stereotype.Service;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.Utils;
import org.web3j.abi.datatypes.DynamicStruct;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.StaticArray;
import org.web3j.abi.datatypes.StaticStruct;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Uint;
import org.web3j.crypto.Hash;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;

public class BsinDefaultFunctionEncoder extends FunctionEncoder {

    @Override
    public String encodeFunction(Function function) {
        List<Type> parameters = function.getInputParameters();
        String methodSignature = buildMethodSignature(function.getName(), parameters);
        String methodId = buildMethodId(methodSignature);
        StringBuilder result = new StringBuilder(methodId);
        return encodeParameters(parameters, result);
    }

    public static String buildMethodId(final String methodSignature) {
        byte[] input = methodSignature.getBytes();
        byte[] hash = Hash.sha3(input);
        return Numeric.toHexString(hash).substring(0, 10);
    }

    public String encodeParameters(List<Type> parameters) {
        return encodeParameters(parameters, new StringBuilder());
    }

    public String encodeWithSelector(String methodId, List<Type> parameters) {
        StringBuilder result = new StringBuilder(methodId);
        return encodeParameters(parameters, result);
    }

    protected String encodePackedParameters(List<Type> parameters) {
        StringBuilder result = new StringBuilder();
        Iterator var3 = parameters.iterator();

        while(var3.hasNext()) {
            Type parameter = (Type)var3.next();
            result.append(BsinTypeEncoder.encodePacked(parameter));
        }

        return result.toString();
    }

    public static String encodeParameters(List<Type> parameters, StringBuilder result) {
        int dynamicDataOffset = getLength(parameters) * 32;
        StringBuilder dynamicData = new StringBuilder();
        Iterator var4 = parameters.iterator();

        while(var4.hasNext()) {
            Type parameter = (Type)var4.next();
            String encodedValue = BsinTypeEncoder.encode(parameter);
            if (BsinTypeEncoder.isDynamic(parameter)) {
                String encodedDataOffset = BsinTypeEncoder.encodeNumeric(new Uint(BigInteger.valueOf((long)dynamicDataOffset)));
                result.append(encodedDataOffset);
                dynamicData.append(encodedValue);
                dynamicDataOffset += encodedValue.length() >> 1;
            } else {
                result.append(encodedValue);
            }
        }

        result.append(dynamicData);
        return result.toString();
    }

    private static int getLength(List<Type> parameters) {
        int count = 0;
        Iterator var2 = parameters.iterator();

        while(true) {
            while(var2.hasNext()) {
                Type type = (Type)var2.next();
                if (type instanceof StaticArray && StaticStruct.class.isAssignableFrom(((StaticArray)type).getComponentType())) {
                    count += Utils.staticStructNestedPublicFieldsFlatList(((StaticArray)type).getComponentType()).size() * ((StaticArray)type).getValue().size();
                } else if (type instanceof StaticArray && DynamicStruct.class.isAssignableFrom(((StaticArray)type).getComponentType())) {
                    ++count;
                } else if (type instanceof StaticArray) {
                    count += ((StaticArray)type).getValue().size();
                } else {
                    ++count;
                }
            }

            return count;
        }
    }




}
