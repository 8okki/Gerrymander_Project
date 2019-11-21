import json

path = "../data/election data ohio precincts.csv"
out_path = "../data/2016_election_ohio.json"

csv = open(path, "r")
out_file = open(out_path, "w")
lines = csv.readlines()[4:]

for line in lines:
	data = line.replace("\n","").split(",")
	data_dict = {"county":data[0], "precinct_name":data[1], "precinct_code":data[2], "total_votes":data[3], "votes":{"dem":data[4], "rep": data[5]}}
	data_json = json.dumps(data_dict)
	if data_dict["county"] == "Gallia":
		out_file.write(data_json + ",\n")
	
csv.close()
out_file.close()
