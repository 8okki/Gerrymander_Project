import geopandas as gp

shpfile = "data/shp/precincts_results.shp"

df = gp.read_file(shpfile)
df["NEIGHBORS"] = None
for index, row in df.iterrows():
    try:
        neighbors = df[df.geometry.touches(row['geometry'])].PRECINCT.tolist()
        neighbors = [name for name in neighbors if row.PRECINCT != name]
    except:
        neighbors = []
    try:
        df.at[index, "NEIGHBORS"] = ", ".join(neighbors)
    except:
        df.at[index, "NEIGHBORS"] = ""
df.to_file("test.shp")
