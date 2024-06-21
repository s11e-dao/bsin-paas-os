package util

import (
	"fmt"
	"os"

	"github.com/gocarina/gocsv"
)

type Client struct { // Our example struct, you can use "-" to ignore a field
	Num          uint32 `csv:"num"`
	PrivateKey   string `csv:"privateKey"`
	PublicKey    string `csv:"publicKey"`
	TransferHash string `csv:"transferHash"`
	TokenId      string `csv:"tokenId"`
	ContractAddr string `csv:"contractAddr"`
	Name         string `csv:"name"`
}

var GlobalCsvClient []*Client

func csvExample() error {
	clientsFile, err := os.OpenFile("clients.csv", os.O_RDWR|os.O_CREATE, os.ModePerm)
	if err != nil {
		fmt.Println("OpenFile:", err)
		return err
	}
	defer clientsFile.Close()
	clients := []*Client{}

	if err := gocsv.UnmarshalFile(clientsFile, &clients); err != nil { // Load clients from file
		fmt.Println("UnmarshalFile:", err)
		return err
	}
	for _, client := range clients {
		fmt.Println("Num", client.Num)
		fmt.Println("PrivateKey", client.PrivateKey)
		fmt.Println("PublicKey", client.PublicKey)
		fmt.Println("TransferHash", client.TransferHash)
		fmt.Println("TokenId", client.TokenId)
		fmt.Println("ContractAddr", client.ContractAddr)
		fmt.Println("Name", client.Name)
	}

	if _, err := clientsFile.Seek(0, 0); err != nil { // Go to the start of the file
		fmt.Println("Seek:", err)
		return err
	}

	clients = append(clients, &Client{Num: 1, PrivateKey: "0x", PublicKey: "0x", TransferHash: "0x", TokenId: "1", ContractAddr: "0x", Name: "创世碎片1"}) // Add clients
	csvContent, err := gocsv.MarshalString(&clients)                                                                                                   // Get all clients as CSV string
	if err != nil {
		fmt.Println("MarshalString:", err)
		return err
	}
	err = gocsv.MarshalFile(&clients, clientsFile) // Use this to save the CSV back to the file
	if err != nil {
		fmt.Println("MarshalFile:", err)
		return err
	}
	fmt.Println(csvContent) // Display all clients as CSV string
	return nil
}

func loadCSV() (error, []*Client) {
	clientsFile, err := os.OpenFile("clients.csv", os.O_RDWR|os.O_CREATE, os.ModePerm)
	if err != nil {
		fmt.Println("OpenFile:", err)
		return err, nil
	}
	defer clientsFile.Close()
	if err := gocsv.UnmarshalFile(clientsFile, &GlobalCsvClient); err != nil { // Load GlobalCsvClient from file
		fmt.Println("UnmarshalFile:", err)
		return err, nil
	}
	for _, client := range GlobalCsvClient {
		fmt.Println("Num", client.Num)
		fmt.Println("PrivateKey", client.PrivateKey)
		fmt.Println("PublicKey", client.PublicKey)
		fmt.Println("TransferHash", client.TransferHash)
		fmt.Println("TokenId", client.TokenId)
		fmt.Println("ContractAddr", client.ContractAddr)
		fmt.Println("Name", client.Name)
	}

	if _, err := clientsFile.Seek(0, 0); err != nil { // Go to the start of the file
		fmt.Println("Seek:", err)
		return err, nil
	}

	// GlobalCsvClient = append(GlobalCsvClient, &Client{Num: 1, PrivateKey: "0x", PublicKey: "0x", TransferHash: "0x", TokenId: "1", ContractAddr: "0x", Name: "创世碎片1"}) // Add GlobalCsvClient
	csvContent, err := gocsv.MarshalString(&GlobalCsvClient) // Get all GlobalCsvClient as CSV string
	if err != nil {
		fmt.Println("MarshalString:", err)
		return err, nil
	}
	err = gocsv.MarshalFile(&GlobalCsvClient, clientsFile) // Use this to save the CSV back to the file
	if err != nil {
		fmt.Println("MarshalFile:", err)
		return err, nil
	}
	fmt.Println(csvContent) // Display all GlobalCsvClient as CSV string
	return nil, GlobalCsvClient
}

// before append need to load csv file
func AppendCSV(client Client) error {

	clientsFile, err := os.OpenFile("clients.csv", os.O_RDWR|os.O_CREATE, os.ModePerm)
	if err != nil {
		fmt.Println("OpenFile:", err)
		return err
	}
	defer clientsFile.Close()

	if _, err := clientsFile.Seek(0, 0); err != nil { // Go to the start of the file
		fmt.Println("Seek:", err)
		return err
	}

	GlobalCsvClient = append(GlobalCsvClient, &client) // Add GlobalCsvClient
	// csvContent, err := gocsv.MarshalString(&GlobalCsvClient) // Get all GlobalCsvClient as CSV string
	// if err != nil {
	// 	// fmt.Println("MarshalString:", err)
	// 	return err
	// }
	// fmt.Println(csvContent)                                // Display all GlobalCsvClient as CSV string
	err = gocsv.MarshalFile(&GlobalCsvClient, clientsFile) // Use this to save the CSV back to the file
	if err != nil {
		// fmt.Println("MarshalFile:", err)
		return err
	}
	return nil
}
