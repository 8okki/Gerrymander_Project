request = require("request-promise");
fs = require("fs");

let url = "https://ogre.adc4gis.com/";

let path = process.argv[2];

(async function() {
	result = await request({
                method: 'POST',
                url: url + 'convert',
                formData : {
					'upload': fs.createReadStream(path)
				}
            });
	console.log(result);
})();
