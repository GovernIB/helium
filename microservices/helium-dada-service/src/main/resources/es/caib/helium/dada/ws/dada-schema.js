db.createCollection("expedient", {
	validator: {
		$jsonSchema: {
			bsonType: "object",
			required: ["expedientId", "entornId", "tipusId", "procesPrincipalId", "dataInici"],
			additionalProperties: false,
			properties: {
				"_id": { bsonType: "objectId" },
				"_class": { bsonType: "string" },
				"expedientId": { bsonType: "long" },
				entornId: { bsonType: "long" },
				tipusId: { bsonType: "long" },
				numero: { bsonType: "string", maxLength: 64 },
				titol: { bsonType: "string", maxLength: 255 },
				procesPrincipalId: { bsonType: "long" },
				estatId: { bsonType: "int" },
				dataInici: { bsonType: "date" },
				dataFi: { bsonType: "date", description: "Data de fi de l'expedient." }
			}
		}
	},
	validationLevel: "strict",
	validationAction: "error"
})

db.runCommand( {
   collMod: "expedient",
	validator: {
		$jsonSchema: {
			bsonType: "object",
			required: ["entornId", "expedientId", "tipusId", "procesPrincipalId", "dataInici"],
			additionalProperties: false,
			properties: {
				"_id": { bsonType: "objectId" },
				"_class": { bsonType: "string" },
				expedientId: { bsonType: "long" },
				entornId: { bsonType: "long" },
				tipusId: { bsonType: "long" },
				numero: { bsonType: "string", maxLength: 64 },
				titol: { bsonType: "string", maxLength: 255 },
				procesPrincipalId: { bsonType: "long" },
				estatId: { bsonType: "int" },
				dataInici: { bsonType: "date" },
				dataFi: { bsonType: "date", description: "Data de fi de l'expedient." }
			}
		}
	},
	validationLevel: "strict",
	validationAction: "error"
} )

db.createCollection("dada", {
	validator: {
		$jsonSchema: {
			bsonType: "object",
			required: ["codi", "tipus", "multiple", "valor", "expedientId", "procesId"],
			additionalProperties: false,
			properties: {
				"_id": { bsonType: "objectId" },
				"_class": { bsonType: "string" },
				"expedientId": { bsonType: "long"},
				"procesId": { bsonType: "long"},
				codi: { bsonType: "string", maxLength: 255 },
				tipus: { bsonType: "string", maxLength: 255 },
				multiple: { bsonType: "bool" },
				valor: {
					bsonType: ["array"],
                                        properties: {"_class" : {bsonType: "string"}},
					/*anyOf: [
						{
							bsonType: "object",
							required: ["valor", "valorText"],
							additionalProperties: false,
							properties: {
								"_class": { bsonType: "string" },
								valor: { bsonType: "string", maxLength: 255 },
								valorText: { bsonType: "string", maxLength: 255 }
							}
						},
						{
							bsonType: "object",
							required: ["camps"],
							additionalProperties: false,
							properties: {
								"_id": { bsonType: "objectId" },
								"_class": { bsonType: "string" },
								camps: { bsonType: "array" }
							}
						} 
					]*/
				},
			},
		}
	},
	validationLevel: "strict",
	validationAction: "error"
})


/*
       {
            "camps" : [ 
                {
                    "codi" : "codi",
                    "tipus" : "String",
                    "multiple" : true,
                    "valor" : [ 
                        {
                            "valor" : "valorProva",
                            "valorText" : "TextProva"
                        }, 
                        {
                            "valor" : "valorProva1",
                            "valorText" : "TextProva1"
                        }
                    ]
                }
            ]
        },
*/