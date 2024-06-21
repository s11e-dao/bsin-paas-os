package util

import (
	"fmt"
	"testing"
)

func TestCSV(t *testing.T) {

	err := csvExample()
	if err != nil {
		t.Fatalf("csvExample failed [%v]", err)
	}
}

func TestLoadCSV(t *testing.T) {
	err, clients := loadCSV()
	if err != nil {
		t.Fatalf("loadCSV failed [%v]", err)
	}
	fmt.Println("**********test clients*******", clients)
	for _, client := range clients {
		fmt.Println("Num", client.Num)
		fmt.Println("PrivateKey", client.PrivateKey)
		fmt.Println("PublicKey", client.PublicKey)
		fmt.Println("TransferHash", client.TransferHash)
		fmt.Println("TokenUri", client.TokenUri)
		fmt.Println("ContractAddr", client.ContractAddr)
		fmt.Println("Name", client.Name)
	}
}

func TestAppendCSV(t *testing.T) {
	err, clients := loadCSV()
	if err != nil {
		t.Fatalf("loadCSV failed [%v]", err)
	}
	fmt.Println("**********test clients*******", clients)
	for _, client := range clients {
		fmt.Println("Num", client.Num)
		fmt.Println("PrivateKey", client.PrivateKey)
		fmt.Println("PublicKey", client.PublicKey)
		fmt.Println("TransferHash", client.TransferHash)
		fmt.Println("TokenUri", client.TokenUri)
		fmt.Println("ContractAddr", client.ContractAddr)
		fmt.Println("Name", client.Name)
	}
	client := Client{Num: 111, PrivateKey: "0x", PublicKey: "0x", TransferHash: "0x", TokenUri: "www.baidu.com", ContractAddr: "0x", Name: "创世碎片1"} // Add clients
	AppendCSV(client)
}
