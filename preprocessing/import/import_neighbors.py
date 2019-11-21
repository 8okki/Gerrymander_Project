import mysql.connector
import json

db = mysql.connector.connect(
            host='mysql4.cs.stonybrook.edu',
            user='jbuckley',
            password='111089268',
            database='mavericks'
)
cursor = db.cursor()

neighbor_query = "INSERT INTO neighbors(name, neighbor_name) values (%s, %s)"
precinct_query = "INSERT INTO precincts (name, code, population, state) values (%s,%s,%s,%s)"
votes_query = "INSERT INTO votes (precinct_name, precinct) values (%s,%s)"
party_votes_query = "INSERT INTO party_votes (precinct_name, votes, politicalparty) values (%s,%s,%s)"
demographics_query = "INSERT INTO demographics (precinct_name, population, demographic) values (%s, %s, %s)"

state = "OHIO"

neighbor_list = open("../../data/gallia.txt").read().splitlines()

for precinct in neighbor_list:
	data = precinct.split(";")
	precinct_name = data[0]
	all_neighbors = data[1:]
	for neighbor in all_neighbors:
		try:
			cursor.execute(neighbor_query,(precinct_name,neighbor))
		except Exception as e:
			db.close()
			raise Exception(e)
db.commit()
db.close()
