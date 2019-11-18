import shapefile
import shapely
from shapely.geometry import shape
from area import area

shapes = shapefile.Reader("shp-convert/precincts.shp")
shapes2 = shapefile.Reader("tabblock-39053/tl_2010_39053_tabblock00.shp")

precinct = shapes.shapeRecords()[0]
precinct_shape = shape(precinct.shape.__geo_interface__)
print(precinct.record[0:])

for s in shapes2.iterShapeRecords():
    block = shape(s.shape.__geo_interface__)
    if block.intersects(precinct_shape):
        intersection = block.intersection(precinct_shape)
        print(s.record[0:],(intersection.area/block.area)*100)
