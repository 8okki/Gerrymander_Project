import json
import math

precinct_blocks_path = "../data/gallia_blocks.json"
block_dist_path = "../data/block_pop_dist.json"

out_path = "../data/gallia_precinct_pops.json"

precinct_blocks = open(precinct_blocks_path, "r")
block_dists = open(block_dist_path, "r")
out_file = open(out_path, "w")

precinct_lines = precinct_blocks.readlines()[0:]
block_lines = block_dists.readlines()[0:]

for precinct_data in precinct_lines:
	precinct_data = precinct_data[:-2]
	precinct_data = json.loads(precinct_data)
	result = {"precinct_name":precinct_data["precinct_name"], 
	"pop_dist":{"WHITE":0,"BLACK":0,"ASIAN":0,"HISPANIC":0},"total_pop":0}
	intersected_blocks = precinct_data["blocks"]
	for block_data in block_lines:
		block_data = block_data[:-2]
		block_data = json.loads(block_data)
		if block_data["block_name"] in intersected_blocks.keys():
			ratio = intersected_blocks[block_data["block_name"]] / 100
			white_pop = math.floor(int(block_data["pop_dist"]["WHITE"])*ratio)
			black_pop = math.floor(int(block_data["pop_dist"]["BLACK"])*ratio)
			asian_pop = math.floor(int(block_data["pop_dist"]["ASIAN"])*ratio)
			hispanic_pop = math.floor(int(block_data["pop_dist"]["HISPANIC"])*ratio)
			total_pop = math.floor(int(block_data["total_pop"])*ratio)
			result["total_pop"] += total_pop
			result["pop_dist"]["WHITE"] += white_pop
			result["pop_dist"]["BLACK"] += black_pop
			result["pop_dist"]["ASIAN"] += asian_pop
			result["pop_dist"]["HISPANIC"] += hispanic_pop
	out_file.write(json.dumps(result) + ",\n")
	
precinct_blocks.close()
block_dists.close()
out_file.close()
