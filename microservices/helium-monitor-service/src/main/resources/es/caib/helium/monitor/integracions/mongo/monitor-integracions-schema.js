
db.createCollection("integracioEvent", {
	validator: {
		$jsonSchema: {
			bsonType: "object",
			required: ["codi", "entornId", "tipus", "data", "estat", "descripcio"],
			properties: {
				"_id": { bsonType: "objectId" },
				"_class": { bsonType: "string" },
				"codi": { enum: ["PERSONA","SISTRA", "DISTRIBUCIO", 
								"PFIRMA", "FIRMA", "CUSTODIA", "REGISTRE", 
								"GESDOC", "CONVDOC", "FIRMA_SERV", "ARXIU", "NOTIB", "VALIDASIG"] },
				entornId: { bsonType: "long" },
				tipus: {enum: ["ENVIAMENT", "RECEPCIO"]},
				data: { bsonType: "date" },
				estat: { enum: ["OK", "ERROR"]},
				descripcio: { bsonType: "string", maxLength: 255 },
				parametres: {
					bsonType: "array",
					items: {
						bsonType: "object", anyOf: [{
							bsonType: "object",
							required: ["nom","valor"],
							additionalProperties: false,
							properties: {
								"_class": { bsonType: "string" },
								nom: { bsonType: "string", maxLength: 255 },
								valor: { bsonType: "string", maxLength: 255 }
							}
						}]
					}
				}
			}
		}
	},
	validationLevel: "strict",
	validationAction: "error"
})