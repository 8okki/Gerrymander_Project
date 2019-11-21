import json

path = "../data/DEC_10_PL_P1_with_ann.csv"
out_path = "../data/block_pop_dist.json"

csv = open(path, "r")
out_file = open(out_path, "w")
lines = csv.readlines()[1:]

for line in lines:
	data = line.replace("\n","").replace('"',"").split(",")
	data_dict = {"block_name":data[2][14:]+"_"+data[0],"pop_dist":{"WHITE":data[6],"BLACK":data[7],"ASIAN":data[8],"HISPANIC":data[9]},"total_pop":data[5]}
	data_json = json.dumps(data_dict)
	out_file.write(data_json + ",\n")
	
csv.close()
out_file.close()
