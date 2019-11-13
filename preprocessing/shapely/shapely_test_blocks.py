import json
import sys
from shapely.geometry import shape, GeometryCollection, Polygon
import matplotlib.pyplot as plt

file_path = sys.argv[1]

with open(file_path) as f:
	features = json.load(f)["features"]

with open("precinct-test.json") as f:
	precinct = json.load(f)["features"]
	
polygons = GeometryCollection([shape(feature["geometry"]).buffer(0) for feature in features])
precinct_polygon = GeometryCollection([shape(feature["geometry"]).buffer(0) for feature in precinct])
print(precinct_polygon)
print(len(polygons))
print("done")