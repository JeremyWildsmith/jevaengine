import Tkinter
import tkFileDialog
import os
import json
from xml.dom import minidom

root = Tkinter.Tk()
root.withdraw()

currdir = os.getcwd()
inputXml = tkFileDialog.askopenfilename(parent=root, initialdir=currdir, title='Please select a NGL FontBuilder XML input file')
output = os.path.dirname(inputXml) + "/font.juif"
dom = minidom.parse(inputXml)


fontSize = int(dom.getElementsByTagName("description")[0].attributes["size"].value)
charEncodings = dom.getElementsByTagName("char")

juif = {}
juif["glyphs"] = []
juif["texture"] = "texture.png"
for e in charEncodings:
    g = {}
    g["char"] = ord(e.attributes['id'].value)
    width = int(e.attributes['rect_w'].value)
    advance = int(e.attributes["advance"].value)

    g["advance"] = advance               
    g["region"] = {
            "x": int(e.attributes['rect_x'].value),
            "y": int(e.attributes['rect_y'].value),
            "width": width,
            "height": int(e.attributes['rect_h'].value)
        }

    g["offset"] = {
            "x": 0,
            "y": fontSize - int(e.attributes['offset_y'].value)
        }

    juif["glyphs"].append(g)

outFile = open(output, "w")
json.dump(juif, outFile)
outFile.close()
#print(json.dumps(juif))
