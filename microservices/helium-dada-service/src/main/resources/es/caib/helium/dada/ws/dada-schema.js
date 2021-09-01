//Comandes per crear les col·leccions necessaries pel servei. També afegeix les validacions per les col·leccions.

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
				procesPrincipalId: { bsonType: "string" },
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
				procesPrincipalId: { bsonType: "string" },
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
				"procesId": { bsonType: "string"},
				codi: { bsonType: "string", maxLength: 255 },
				tipus: { enum: ["Long", "String", "Date", "Float", "Termini", "Preu", "Integer", "Boolean", "Registre"]},
				multiple: { bsonType: "bool" },
				valor: {
					bsonType: "array",
					items: {bsonType: "object", anyOf: [
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
                                "_class": { bsonType: "string" },
                                camps: { 
                                    bsonType: "array",
                                    items: {
                                        bsonType: "object",
                                        required: ["codi", "tipus", "multiple", "valor"],
                                        additionalProperties: false,
                                        properties: {
                                            "_class": { bsonType: "string" },
                                            codi: { bsonType: "string", maxLength: 255 },
                                            tipus: { enum: ["Long", "String", "Date", "Float", "Termini", "Preu", "Integer", "Boolean", "Registre"], maxLength: 255 },
                                            multiple: { bsonType: "bool" },
                                            valor: {
                                                bsonType: "array", 
                                                items: {
                                                    bsonType: "object",
                                                    required: ["valor", "valorText"],
                                                    additionalProperties: false,
                                                    properties: {
                                                        "_class": { bsonType: "string" },
                                                        valor: { bsonType: "string", maxLength: 255},
                                                        valorText: { bsonType: "string", maxLength: 255}
                                                    }
                                                }
                                            }
                                        }
                                    } 
                            }
                        }
						}]
                    }
				},
			},
		}
	},
	validationLevel: "strict",
	validationAction: "error"
})