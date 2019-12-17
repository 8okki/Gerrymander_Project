import json

rewrite = True
with open("OHIO_PRECINCTS.json") as json_file:
    data = json.load(json_file)
    wData = data['features']
    nData = []
    for x in wData:
        i = x['properties']
        nData.append({
            "type": "Feature",
            "properties": {
                'PRECODE':i['PRECODE'],
                'TOTVOTE16':i['TOTVOTE16'],
                'PRES16R':i['PRES16R'],
                'PRES16D':i['PRES16D'],
                'TOTPOP':i['TOTPOP'],
                'NH_WHITE':i['NH_WHITE'],
                'NH_BLACK':i['NH_BLACK'],
                'NH_ASIAN':i['NH_ASIAN'],
                'HISP':i['HISP']
                },
              "geometry":x['geometry']
            })
if rewrite:
    with open("OHIO_CLEANED.json",'w') as newFile:
        json.dump({"type":'FeatureCollection',"features":nData}, newFile, separators=(',',':'),indent=None)
