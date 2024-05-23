//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//
package me.flyray.bsin.blockchain.utils;

import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Array;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Bytes;
import org.web3j.abi.datatypes.BytesType;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.DynamicBytes;
import org.web3j.abi.datatypes.DynamicStruct;
import org.web3j.abi.datatypes.Fixed;
import org.web3j.abi.datatypes.FixedPointType;
import org.web3j.abi.datatypes.NumericType;
import org.web3j.abi.datatypes.StaticArray;
import org.web3j.abi.datatypes.StaticStruct;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Ufixed;
import org.web3j.abi.datatypes.Uint;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.primitive.PrimitiveType;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class BsinTypeEncoder {
    private BsinTypeEncoder() {
    }

    static boolean isDynamic(Type parameter) {
        return parameter instanceof DynamicBytes || parameter instanceof Utf8String || parameter instanceof DynamicArray || parameter instanceof StaticArray && DynamicStruct.class.isAssignableFrom(((StaticArray)parameter).getComponentType());
    }

    public static String encode(Type parameter) {
        if (parameter instanceof NumericType) {
            return encodeNumeric((NumericType)parameter);
        } else if (parameter instanceof Address) {
            return encodeAddress((Address)parameter);
        } else if (parameter instanceof Bool) {
            return encodeBool((Bool)parameter);
        } else if (parameter instanceof Bytes) {
            return encodeBytes((Bytes)parameter);
        } else if (parameter instanceof DynamicBytes) {
            return encodeDynamicBytes((DynamicBytes)parameter);
        } else if (parameter instanceof Utf8String) {
            return encodeString((Utf8String)parameter);
        } else if (parameter instanceof StaticArray) {
            return DynamicStruct.class.isAssignableFrom(((StaticArray)parameter).getComponentType()) ? encodeStaticArrayWithDynamicStruct((StaticArray)parameter) : encodeArrayValues((StaticArray)parameter);
        } else if (parameter instanceof DynamicStruct) {
            return encodeDynamicStruct((DynamicStruct)parameter);
        } else if (parameter instanceof DynamicArray) {
            return encodeDynamicArray((DynamicArray)parameter);
        } else if (parameter instanceof PrimitiveType) {
            return encode(((PrimitiveType)parameter).toSolidityType());
        } else {
            throw new UnsupportedOperationException("Type cannot be encoded: " + parameter.getClass());
        }
    }

    public static String encodePacked(Type parameter) {
        if (parameter instanceof Utf8String) {
            return removePadding(encode(parameter), parameter);
        } else if (parameter instanceof DynamicBytes) {
            return encode(parameter).substring(64);
        } else if (parameter instanceof DynamicArray) {
            return arrayEncodePacked((DynamicArray)parameter);
        } else if (parameter instanceof StaticArray) {
            return arrayEncodePacked((StaticArray)parameter);
        } else {
            return parameter instanceof PrimitiveType ? encodePacked(((PrimitiveType)parameter).toSolidityType()) : removePadding(encode(parameter), parameter);
        }
    }

    static String removePadding(String encodedValue, Type parameter) {
        if (parameter instanceof NumericType) {
            return !(parameter instanceof Ufixed) && !(parameter instanceof Fixed) ? encodedValue.substring(64 - ((NumericType)parameter).getBitSize() / 4, 64) : encodedValue;
        } else if (parameter instanceof Address) {
            return encodedValue.substring(64 - ((Address)parameter).toUint().getBitSize() / 4, 64);
        } else if (parameter instanceof Bool) {
            return encodedValue.substring(62, 64);
        } else if (parameter instanceof Bytes) {
            return encodedValue.substring(0, ((BytesType)parameter).getValue().length * 2);
        } else if (parameter instanceof Utf8String) {
            int length = ((Utf8String)parameter).getValue().getBytes(StandardCharsets.UTF_8).length;
            return encodedValue.substring(64, 64 + length * 2);
        } else {
            throw new UnsupportedOperationException("Type cannot be encoded: " + parameter.getClass());
        }
    }

    private static <T extends Type> String encodeStaticArrayWithDynamicStruct(Array<T> value) {
        String valuesOffsets = encodeStructsArraysOffsets(value);
        String encodedValues = encodeArrayValues(value);
        StringBuilder result = new StringBuilder();
        result.append(valuesOffsets);
        result.append(encodedValues);
        return result.toString();
    }

    static String encodeAddress(Address address) {
        return encodeNumeric(address.toUint());
    }

    static String encodeNumeric(NumericType numericType) {
        byte[] rawValue = toByteArray(numericType);
        byte paddingValue = getPaddingValue(numericType);
        byte[] paddedRawValue = new byte[32];
        if (paddingValue != 0) {
            for(int i = 0; i < paddedRawValue.length; ++i) {
                paddedRawValue[i] = paddingValue;
            }
        }

        System.arraycopy(rawValue, 0, paddedRawValue, 32 - rawValue.length, rawValue.length);
        return Numeric.toHexStringNoPrefix(paddedRawValue);
    }

    private static byte getPaddingValue(NumericType numericType) {
        return (byte)(numericType.getValue().signum() == -1 ? -1 : 0);
    }

    private static byte[] toByteArray(NumericType numericType) {
        BigInteger value = numericType.getValue();
        if ((numericType instanceof Ufixed || numericType instanceof Uint) && value.bitLength() == 256) {
            byte[] byteArray = new byte[32];
            System.arraycopy(value.toByteArray(), 1, byteArray, 0, 32);
            return byteArray;
        } else {
            return value.toByteArray();
        }
    }

    static String encodeBool(Bool value) {
        byte[] rawValue = new byte[32];
        if (value.getValue()) {
            rawValue[rawValue.length - 1] = 1;
        }

        return Numeric.toHexStringNoPrefix(rawValue);
    }

    static String encodeBytes(BytesType bytesType) {
        byte[] value = bytesType.getValue();
        int length = value.length;
        int mod = length % 32;
        byte[] dest;
        if (mod != 0) {
            int padding = 32 - mod;
            dest = new byte[length + padding];
            System.arraycopy(value, 0, dest, 0, length);
        } else {
            dest = value;
        }

        return Numeric.toHexStringNoPrefix(dest);
    }

    static String encodeDynamicBytes(DynamicBytes dynamicBytes) {
        int size = dynamicBytes.getValue().length;
        String encodedLength = encode(new Uint(BigInteger.valueOf((long)size)));
        String encodedValue = encodeBytes(dynamicBytes);
        StringBuilder result = new StringBuilder();
        result.append(encodedLength);
        result.append(encodedValue);
        return result.toString();
    }

    static String encodeString(Utf8String string) {
        byte[] utfEncoded = string.getValue().getBytes(StandardCharsets.UTF_8);
        return encodeDynamicBytes(new DynamicBytes(utfEncoded));
    }

    static <T extends Type> String encodeArrayValues(Array<T> value) {
        StringBuilder result = new StringBuilder();
        Iterator var2 = value.getValue().iterator();

        while(var2.hasNext()) {
            Type type = (Type)var2.next();
            result.append(encode(type));
        }

        return result.toString();
    }

    static String encodeDynamicStruct(DynamicStruct value) {
        String encodedValues = encodeDynamicStructValues(value);
        StringBuilder result = new StringBuilder();
        result.append(encodedValues);
        return result.toString();
    }

    private static String encodeDynamicStructValues(DynamicStruct value) {
        int staticSize = 0;

        int dynamicOffset;
        for(dynamicOffset = 0; dynamicOffset < value.getValue().size(); ++dynamicOffset) {
            Type type = (Type)value.getValue().get(dynamicOffset);
            if (isDynamic(type)) {
                staticSize += 32;
            } else {
                staticSize += type.bytes32PaddedLength();
            }
        }

        dynamicOffset = staticSize;
        List<String> offsetsAndStaticValues = new ArrayList();
        List<String> dynamicValues = new ArrayList();

        for(int i = 0; i < value.getValue().size(); ++i) {
            Type type = (Type)value.getValue().get(i);
            if (isDynamic(type)) {
                offsetsAndStaticValues.add(Numeric.toHexStringNoPrefix(Numeric.toBytesPadded(new BigInteger(Long.toString((long)dynamicOffset)), 32)));
                String encodedValue = encode(type);
                dynamicValues.add(encodedValue);
                dynamicOffset += encodedValue.length() >> 1;
            } else {
                offsetsAndStaticValues.add(encode((Type)value.getValue().get(i)));
            }
        }

        List<String> data = new ArrayList();
        data.addAll(offsetsAndStaticValues);
        data.addAll(dynamicValues);
        return String.join("", data);
    }

    static <T extends Type> String encodeDynamicArray(DynamicArray<T> value) {
        int size = value.getValue().size();
        String encodedLength = encode(new Uint(BigInteger.valueOf((long)size)));
        String valuesOffsets = encodeArrayValuesOffsets(value);
        String encodedValues = encodeArrayValues(value);
        StringBuilder result = new StringBuilder();
        result.append(encodedLength);
        result.append(valuesOffsets);
        result.append(encodedValues);
        return result.toString();
    }

    private static <T extends Type> String encodeArrayValuesOffsets(DynamicArray<T> value) {
        StringBuilder result = new StringBuilder();
        boolean arrayOfBytes = !value.getValue().isEmpty() && value.getValue().get(0) instanceof DynamicBytes;
        boolean arrayOfString = !value.getValue().isEmpty() && value.getValue().get(0) instanceof Utf8String;
        boolean arrayOfDynamicStructs = !value.getValue().isEmpty() && value.getValue().get(0) instanceof DynamicStruct;
        if (!arrayOfBytes && !arrayOfString) {
            if (arrayOfDynamicStructs) {
                result.append(encodeStructsArraysOffsets(value));
            }
        } else {
            long offset = 0L;

            for(int i = 0; i < value.getValue().size(); ++i) {
                if (i == 0) {
                    offset = (long)(value.getValue().size() * 32);
                } else {
                    int bytesLength = arrayOfBytes ? ((byte[])((byte[])((Type)value.getValue().get(i - 1)).getValue())).length : ((String)((Type)value.getValue().get(i - 1)).getValue()).length();
                    int numberOfWords = (bytesLength + 32 - 1) / 32;
                    int totalBytesLength = numberOfWords * 32;
                    offset += (long)(totalBytesLength + 32);
                }

                result.append(Numeric.toHexStringNoPrefix(Numeric.toBytesPadded(new BigInteger(Long.toString(offset)), 32)));
            }
        }

        return result.toString();
    }

    private static <T extends Type> String encodeStructsArraysOffsets(Array<T> value) {
        StringBuilder result = new StringBuilder();
        long offset = (long)value.getValue().size();
        List<String> tailsEncoding = (List)value.getValue().stream().map(BsinTypeEncoder::encode).collect(Collectors.toList());

        for(int i = 0; i < value.getValue().size(); ++i) {
            if (i == 0) {
                offset *= 32L;
            } else {
                offset += (long)(((String)tailsEncoding.get(i - 1)).length() / 2);
            }

            result.append(Numeric.toHexStringNoPrefix(Numeric.toBytesPadded(new BigInteger(Long.toString(offset)), 32)));
        }

        return result.toString();
    }

    private static <T extends Type> boolean isSupportingEncodedPacked(Array<T> value) {
        return !Utf8String.class.isAssignableFrom(value.getComponentType()) && !DynamicStruct.class.isAssignableFrom(value.getComponentType()) && !DynamicArray.class.isAssignableFrom(value.getComponentType()) && !StaticStruct.class.isAssignableFrom(value.getComponentType()) && !FixedPointType.class.isAssignableFrom(value.getComponentType()) && !DynamicBytes.class.isAssignableFrom(value.getComponentType());
    }

    private static <T extends Type> String arrayEncodePacked(Array<T> values) {
        if (isSupportingEncodedPacked(values)) {
            if (values.getValue().isEmpty()) {
                return "";
            }

            if (values instanceof DynamicArray) {
                return encode(values).substring(64);
            }

            if (values instanceof StaticArray) {
                return encode(values);
            }
        }

        throw new UnsupportedOperationException("Type cannot be packed encoded: " + values.getClass());
    }
}
