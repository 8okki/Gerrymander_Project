import shapefile
import shapely
from shapely.geometry import shape
import json

shapes = shapefile.Reader("../data/precinct-shapes/precincts.shp")
shapes2 = shapefile.Reader("../data/tabblock-39053/tl_2010_39053_tabblock00.shp")
shapes3 = shapefile.Reader("../data/tabblock-39053/tl_2010_39053_tabblock10.shp")
out_path = "../data/gallia_blocks.json"
out_file = open(out_path, "w")

for precinct_sr in shapes.iterShapeRecords():
	precinct_county = precinct_sr.record[2]
	if precinct_county == "gallia":
		precinct_name = precinct_sr.record[1]
		result = {"precinct_name":precinct_name,"blocks":{}}
		precinct_shape = shape(precinct_sr.shape.__geo_interface__)
		
		for s in shapes2.iterShapeRecords():
			block = shape(s.shape.__geo_interface__)
			if block.intersects(precinct_shape):
				block_name = s.record[2][:-2]+"_"+s.record[5]
				intersection = block.intersection(precinct_shape)
				ratio = (intersection.area/block.area)*100
				result["blocks"][block_name] = ratio
		for s in shapes3.iterShapeRecords():
			block = shape(s.shape.__geo_interface__)
			if block.intersects(precinct_shape):
				block_name = s.record[2][:-2]+"_"+s.record[5]
				intersection = block.intersection(precinct_shape)
				ratio = (intersection.area/block.area)*100
				result["blocks"][block_name] = ratio
		out_file.write(json.dumps(result) + ",\n")

out_file.close()