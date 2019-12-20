import mysql.connector
import json

db = mysql.connector.connect(
            host='mysql4.cs.stonybrook.edu',
            user='jbuckley',
            password='111089268',
            database='mavericks'
)
cursor = db.cursor()

precinct_query = "INSERT INTO precincts (code, name, population, state, geojson) values (%s,%s,%s,%s,%s)"
votes_query = "INSERT INTO votes (precinct_code, precinct) values (%s,%s)"
party_votes_query = "INSERT INTO party_votes (precinct_code, votes, politicalparty) values (%s,%s,%s)"
demographics_query = "INSERT INTO demographics (precinct_code, population, demographic) values (%s, %s, %s)"

state = "OHIO"

with open("../../data/ohio_precincts.json") as data:
	line = data.readline().replace("\n", "")
	while line != "":
		precinct_json = json.loads(line)
		properties = precinct_json["properties"]
		code = properties["PRECODE"]
		name = properties["PRECINCT"]
		population = properties["TOTPOP"]
		repvotes = properties["PRES16R"]
		demvotes = properties["PRES16D"]
		whitepop = properties["NH_WHITE"]
		blackpop = properties["NH_BLACK"]
		hispanicpop = properties["HISP"]
		asianpop = properties["NH_ASIAN"]
		
		cursor.execute(precinct_query, (code,name,population,state,line))
		cursor.execute(votes_query, (code, code))
		cursor.execute(party_votes_query, (code, demvotes, "DEMOCRATIC"))
		cursor.execute(party_votes_query, (code, repvotes, "REPUBLICAN"))
		
		cursor.execute(demographics_query, (code, whitepop, "WHITE"))
		cursor.execute(demographics_query, (code, blackpop, "BLACK"))
		cursor.execute(demographics_query, (code, asianpop, "ASIAN"))
		cursor.execute(demographics_query, (code, hispanicpop, "HISPANIC"))
	
		line = data.readline().replace("\n", "")
db.commit()
db.close()
