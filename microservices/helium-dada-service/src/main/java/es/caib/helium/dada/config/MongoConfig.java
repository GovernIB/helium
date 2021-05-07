package es.caib.helium.dada.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

/**
 * Classe encarregada de carregar la configuració de la base de dades de MongoDB
 * Necessaria per poder utilitzar @Transaction en els repositoris custom. 
 */
@Configuration
@EnableMongoRepositories(basePackages = "es.caib.helium.dada.repository")
public class MongoConfig extends AbstractMongoClientConfiguration {

	@Autowired
	private Environment env;

	@Bean
	MongoTransactionManager transactionManager(MongoDatabaseFactory dbFactory) {
		return new MongoTransactionManager(dbFactory);
	}

	@Override
	protected String getDatabaseName() {
		return env.getProperty("spring.data.mongodb.database");
	}

	@Override
	public MongoClient mongoClient() {
		ConnectionString connectionString = new ConnectionString(env.getProperty("spring.data.mongodb.uri"));
		MongoClientSettings mongoClientSettings = MongoClientSettings.builder().applyConnectionString(connectionString)
				.build();

		return MongoClients.create(mongoClientSettings);
	}
}
