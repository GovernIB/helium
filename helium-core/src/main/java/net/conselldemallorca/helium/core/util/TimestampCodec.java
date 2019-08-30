//package net.conselldemallorca.helium.core.util;
//
//import java.sql.Timestamp;
//
//import org.bson.BsonReader;
//import org.bson.BsonWriter;
//import org.bson.codecs.Codec;
//import org.bson.codecs.DecoderContext;
//import org.bson.codecs.EncoderContext;
//
//public class TimestampCodec implements Codec<Timestamp> {
//
//	@Override
//	public void encode(BsonWriter writer, Timestamp value, EncoderContext encoderContext) {
//		if (value == null)
//			writer.writeNull();
//		else
//			writer.writeDateTime(value.getTime());
//	}
//
//	@Override
//	public Timestamp decode(BsonReader reader, DecoderContext decoderContext) {
//		return new Timestamp(reader.readDateTime());
//	}
//
//	@Override
//	public Class<Timestamp> getEncoderClass() {
//		return Timestamp.class;
//	   }
//}