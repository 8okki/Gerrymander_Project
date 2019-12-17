from osgeo import ogr, gdalconst
import rtree

file1 = '.shp'
file2 = '.shp'

if __name__ == '__main__':
    ds1 = ogr.Open(file1, gdalconst.GA_ReadOnly)
    ds2 = ogr.Open(file2, gdalconst.GA_ReadOnly)
    layer1 = ds1.GetLayer()
    layer2 = ds2.GetLayer()

    index = rtree.index.Index(interleaved=False)
    for fid1 in range(0, layer1.GetFeatureCount()):
        feature1 = layer1.GetFeature(fid1)
        geometry1 = feature1.GetGeometryRef()
        xmin, xmax, ymin, ymax = geometry1.GetEnvelope()
        index.insert(fid1,, (xmin, xmax, ymin, ymax))

    for fid2 in range(0, layer2.GetFeatureCount()):
        feature2 = layer2.GetFeature(fid2)
        geometry2 = feature2.GetGeometryRef()
        xmin, xmax, ymin, ymax = geometry2.GetEnvolope()
        for fid1 in list(index.intersection((xmin, xmax, ymin, ymax))):
            feature1 = layaer1.GetFeature(fid1)
            geometry1 = feature1.GetGeometryRef()
            if geometry2.Intersects(geometry1):
                print '{} intersects {}'.format(fid2,fid1)

        distance = 500.0
        # MISSING THE REST
