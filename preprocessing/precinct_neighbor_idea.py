import shapefile
import shapely
from shapely.geometry import shape, MultiPolygon, Polygon
from shapely.strtree import STRtree
import sys

shapes = shapefile.Reader("../data/precinct-shapes/precincts.shp")

#print(type(shapes.iterShapeRecords()))
converted = []
for shape in shapes.iterShapes():
	try:
		converted.append(shape.__geo_interface__)
	except Exception as e:
		print(e)
print("converted",Polygon(converted[0]["coordinates"]))
st = STRtree(converted[0:3])
print(st)

for precinct1_sr in shapes.iterShapeRecords():
	precinct_county = precinct1_sr.record[2]
	if precinct_county == "gallia":
		print(precinct1_sr)
		for precinct2_sr in shapes.iterShapeRecords():
			if precinct1_sr.record[5] != precinct2_sr.record[5] and precinct2_sr.record[2] == precinct_county:
				print(precinct1_sr.record[5],precinct2_sr.record[5])
				precinct_name = precinct1_sr.record[1]
				precinct1_shape = shape(precinct1_sr.shape.__geo_interface__)
				precinct2_shape = shape(precinct2_sr.shape.__geo_interface__)
				if precinct1_shape.boundary.intersects(precinct2_shape.boundary):
					print("hello")
					print(precinct1_shape.boundary.intersection(precinct2_shape.boundary).length*111000)
				
				
				#for s in shapes2.iterShapeRecords():
				#	block = shape(s.shape.__geo_interface__)
				#	if block.intersects(precinct_shape):
		break
