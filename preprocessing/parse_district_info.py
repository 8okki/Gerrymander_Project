import json

with open("../data/Ohio_District_all.csv") as f, open("out_district_info.json","w") as j:
	lines = f.readlines()
	lines = lines[1:]
	districts = {}
	for i in range(1,17):
		districts["district"+str(i)] = {}
	
	for line in lines:
		split_line = line.split(",")
		demo = split_line[2]
		if "total pop" in demo.lower():
			demo = "TOTPOP"
		elif "white" in demo.lower():
			demo = "NH_WHITE"
		elif "black" in demo.lower():
			demo = "NH_BLACK"
		elif "asian" in demo.lower():
			demo = "NH_ASIAN"
		elif "hispanic" in demo.lower():
			demo = "HISPANIC"
		district = 1
		for index in range(3,len(split_line),2):
			districts["district"+str(district)][demo] = int(split_line[index])
			district += 1
	for district in districts:
		j.write(json.dumps(districts[district]) + "\n")