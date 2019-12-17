import fiona
from shapely.geometry import shape
import rtree
import json

filename1 = 'AL_Shapefile/al_final.shp'
filename2 = 'AL_blocks/tl_2010_01_tabblock10.shp'

area_count = 0
length_count = 0
precinctDict = {}
if __name__ == '__main__':
    with fiona.open(filename1, 'r') as layer1:
        with fiona.open(filename2, 'r') as layer2:

            index = rtree.index.Index()
            for feat1 in layer1:
                fid = int(feat1['id'])
                precinctDict[feat1['properties']['COUNTYFP10'] + '-' + feat1['properties']['NAME10']] = {}
                geom1 = shape(feat1['geometry'])
                index.insert(fid, geom1.bounds)

            for feat2 in layer2:
                #print("NEXT")
                geom2 = shape(feat2['geometry'])
                block_name = feat2['properties']['COUNTYFP10'] + "-" + feat2['properties']['NAME10']
                output = []
                for fid in list(index.intersection(geom2.bounds)):
                    feat1 = layer1[fid]
                    geom1 = shape(feat1['geometry'])
                    if geom1.intersects(geom2):
                        x = geom1.intersection(geom2)
                        intersection_area = x.area
                        ratio = (intersection_area/geom2.area)*100
                        precinctDict[feat1['properties']['COUNTYFP10'] + "-" + feat1['properties']['NAME10']][block_name] = ratio
                        """if intLen == 0:
                            #print("AREA: ", x.area)
                            area_count += 1
                        else:
                            #print("LENGTH: ", intLen)
                            length_count += 1
                            f1Name = feat1['properties']['PRECODE']
                            f2Name = feat2['properties']['PRECODE']
                            if f1Name != f2Name and intLen > 100:
                                precinctDict[f1Name].append(f2Name)
                        """
            print(json.dumps(precinctDict))
                        #output.append(feat2['id'])
                #print('{} intersects {}'.format(output, feat1['id']))
                        #print('{} intersects {}'.format(feat2['id'], feat1['id']))
    #print("area_count:",area_count)
    #print("length_count:",length_count)
    #for x in precinctDict:
    #    print("" + x + "," + ";".join(precinctDict[x]))
