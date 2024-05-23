package me.flyray.bsin.blockchain.utils;

import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.DynamicStruct;

import java.util.List;

public class BasicAddressesStruct extends DynamicStruct {
    public List<Address> addr;

    public BasicAddressesStruct(List<Address> addr) {
        super(new DynamicArray<Address>(addr));
        this.addr = addr;
    }

    public BasicAddressesStruct(DynamicArray<Address> addr) {
        super(addr);
        this.addr = addr.getValue();
    }

}
