import geopandas as gp

shpfile = "../data/precinct-shapes/precincts.shp"
df = gp.read_file(shpfile)

print(type(df['geometry']))

