package util

import "bsinpass/go/common"

// Setup Initialize the util
func Setup() {
	jwtSecret = []byte(common.GlobalConf.JwtSecret)
	_, GlobalCsvClient = loadCSV()
}
