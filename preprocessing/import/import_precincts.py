import mysql.connector
import json

db = mysql.connector.connect(
            host='mysql4.cs.stonybrook.edu',
            user='jbuckley',
            password='111089268',
            database='mavericks'
)
cursor = db.cursor()

precinct_query = "INSERT INTO precincts (name, code, population, state) values (%s,%s,%s,%s)"
votes_query = "INSERT INTO votes (precinct_name, precinct) values (%s,%s)"
party_votes_query = "INSERT INTO party_votes (precinct_name, votes, politicalparty) values (%s,%s,%s)"
demographics_query = "INSERT INTO demographics (precinct_name, population, demographic) values (%s, %s, %s)"

state = "OHIO"

with open("../../data/2016_election_ohio.json") as data, open("../../data/gallia_precinct_pops.json") as dist_data:
	line = data.readline().replace("\n", "")[:-1]
	while line != "":
		precinct_json = json.loads(line)
		try:
			precinct_county = precinct_json["county"]
			precinct_name = precinct_json["precinct_name"]
			precinct_code = precinct_json["precinct_code"]
			precinct_votes = int(precinct_json["total_votes"])
			precinct_dem_votes = int(precinct_json["votes"]["dem"])
			precinct_rep_votes = int(precinct_json["votes"]["rep"])
			
			if precinct_county == "Gallia":
				dist_line = dist_data.readline().replace("\n","")[:-1]
				dist_json = json.loads(dist_line)
				population = dist_json["total_pop"]
				white_pop = dist_json["pop_dist"]["WHITE"]
				black_pop = dist_json["pop_dist"]["BLACK"]
				asian_pop = dist_json["pop_dist"]["ASIAN"]
				hispanic_pop = dist_json["pop_dist"]["HISPANIC"]
				
				cursor.execute(precinct_query, (precinct_county+"_"+precinct_name,precinct_code,population,state))
				cursor.execute(votes_query, (precinct_county+"_"+precinct_name, precinct_county+"_"+precinct_name))
				cursor.execute(party_votes_query, (precinct_county+"_"+precinct_name, precinct_dem_votes, "DEMOCRATIC"))
				cursor.execute(party_votes_query, (precinct_county+"_"+precinct_name, precinct_rep_votes, "REPUBLICAN"))
				
				cursor.execute(demographics_query, (precinct_county+"_"+precinct_name, white_pop, "WHITE"))
				cursor.execute(demographics_query, (precinct_county+"_"+precinct_name, black_pop, "BLACK"))
				cursor.execute(demographics_query, (precinct_county+"_"+precinct_name, asian_pop, "ASIAN"))
				cursor.execute(demographics_query, (precinct_county+"_"+precinct_name, hispanic_pop, "HISPANIC"))
				
		except Exception as e:
			db.close()
			raise Exception(e)
		line = data.readline().replace("\n", "")[:-1]
db.commit()
db.close()
