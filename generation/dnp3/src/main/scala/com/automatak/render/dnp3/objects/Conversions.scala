package com.automatak.render.dnp3.objects

import com.automatak.render._
import com.automatak.render.cpp._

class ArbitraryConversion(name: String, incHeaders: List[String], cppHeaders: List[String]) extends Conversion {

  def target: String = name

  def includeHeaders: List[String] = incHeaders
  def implHeaders: List[String] = cppHeaders

  def signatures : Iterator[String] = {
    Iterator(
      "typedef %s Target;".format(name),
      "static bool ReadTarget(openpal::ReadBufferView&, %s&);".format(name),
      "static bool WriteTarget(const %s&, openpal::WriteBufferView&);".format(name)
    )
  }


  def impl(fs: FixedSize)(implicit indent: Indentation): Iterator[String] = {

    def args : String = fs.fields.map(f => "gv."+f.name).mkString(", ")

    def readFunc = {
      val args =  fs.fields.map(f => "value." + f.name).mkString(", ")
      Iterator("bool %s::ReadTarget(ReadBufferView& buff, %s& output)".format(fs.name, name)) ++ bracket {
        Iterator("%s value;".format(fs.name)) ++
        Iterator("if(Read(buff, value))") ++ bracket {
          Iterator("output = %sFactory::From(%s);".format(name, args)) ++
          Iterator("return true;")
        } ++
        Iterator("else") ++ bracket {
          Iterator("return false;")
        }
      }
    }

    def writeFunc = {
      Iterator("bool " + fs.name + "::WriteTarget(const " + name + "& value, openpal::WriteBufferView& buff)") ++ bracket {
        Iterator("return %s::Write(Convert%s::Apply(value), buff);".format(fs.name, fs.name))
      }
    }

    readFunc ++ space ++ writeFunc
  }

}

object ConversionHeaders {

  val dataTypes = quoted("opendnp3/app/MeasurementTypes.h")
  val timeAndInterval = quoted("opendnp3/app/TimeAndInterval.h")
  val crob = quoted("opendnp3/app/ControlRelayOutputBlock.h")
  val ao = quoted("opendnp3/app/AnalogOutput.h")
  val binaryCommandEvent = quoted("opendnp3/app/BinaryCommandEvent.h")
  val analogCommandEvent = quoted("opendnp3/app/AnalogCommandEvent.h")
  val factory = quoted("opendnp3/app/MeasurementFactory.h")
  val serializer = quoted("opendnp3/app/DNP3Serializer.h")
  val conversions = quoted("opendnp3/app/WriteConversions.h")

  val cppIncludes = List(factory, conversions)
}

import ConversionHeaders._

object BinaryConversion extends ArbitraryConversion("Binary", List(serializer, dataTypes), cppIncludes)
object DoubleBitBinaryConversion extends ArbitraryConversion("DoubleBitBinary", List(serializer, dataTypes), cppIncludes)
object AnalogConversion extends ArbitraryConversion("Analog", List(serializer, dataTypes), cppIncludes)
object CounterConversion extends ArbitraryConversion("Counter", List(serializer, dataTypes), cppIncludes)
object FrozenCounterConversion extends ArbitraryConversion("FrozenCounter", List(serializer, dataTypes), cppIncludes)
object BinaryOutputStatusConversion extends ArbitraryConversion("BinaryOutputStatus", List(serializer, dataTypes), cppIncludes)
object AnalogOutputStatusConversion extends ArbitraryConversion("AnalogOutputStatus", List(serializer, dataTypes), cppIncludes)
object CrobConversion extends ArbitraryConversion("ControlRelayOutputBlock", List(serializer, crob), cppIncludes)
object AnalogOutputInt16Conversion extends ArbitraryConversion("AnalogOutputInt16", List(serializer, ao), cppIncludes)
object AnalogOutputInt32Conversion extends ArbitraryConversion("AnalogOutputInt32", List(serializer, ao), cppIncludes)
object AnalogOutputFloat32Conversion extends ArbitraryConversion("AnalogOutputFloat32", List(serializer, ao), cppIncludes)
object AnalogOutputDouble64Conversion extends ArbitraryConversion("AnalogOutputDouble64", List(serializer, ao), cppIncludes)
object TimeAndIntervalConversion extends ArbitraryConversion("TimeAndInterval", List(serializer,timeAndInterval), cppIncludes)
object BinaryCommandEventConversion extends ArbitraryConversion("BinaryCommandEvent", List(serializer,binaryCommandEvent), cppIncludes)
object AnalogCommandEventConversion extends ArbitraryConversion("AnalogCommandEvent", List(serializer,analogCommandEvent), cppIncludes)

trait ConversionToBinary {
  self : FixedSize =>
  override def conversion: Option[Conversion] = Some(BinaryConversion)
}

trait ConversionToDoubleBitBinary {
  self : FixedSize =>
  override def conversion: Option[Conversion] = Some(DoubleBitBinaryConversion)
}
trait ConversionToAnalog {
  self : FixedSize =>
  override def conversion: Option[Conversion] = Some(AnalogConversion)
}

trait ConversionToCounter {
  self : FixedSize =>
  override def conversion: Option[Conversion] = Some(CounterConversion)
}

trait ConversionToFrozenCounter {
  self : FixedSize =>
  override def conversion: Option[Conversion] = Some(FrozenCounterConversion)
}


trait ConversionToBinaryOutputStatus {
  self : FixedSize =>
  override def conversion: Option[Conversion] = Some(BinaryOutputStatusConversion)
}

trait ConversionToAnalogOutputStatus {
  self : FixedSize =>
  override def conversion: Option[Conversion] = Some(AnalogOutputStatusConversion)
}

trait ConversionToCROB {
  self : FixedSize =>
  override def conversion: Option[Conversion] = Some(CrobConversion)
}

trait ConversionToAnalogOutputInt16 {
  self : FixedSize =>
  override def conversion: Option[Conversion] = Some(AnalogOutputInt16Conversion)
}

trait ConversionToAnalogOutputInt32 {
  self : FixedSize =>
  override def conversion: Option[Conversion] = Some(AnalogOutputInt32Conversion)
}

trait ConversionToAnalogOutputFloat32 {
  self : FixedSize =>
  override def conversion: Option[Conversion] = Some(AnalogOutputFloat32Conversion)
}

trait ConversionToAnalogOutputDouble64 {
  self : FixedSize =>
  override def conversion: Option[Conversion] = Some(AnalogOutputDouble64Conversion)
}

trait ConversionToTimeAndInterval {
  self : FixedSize =>
  override def conversion: Option[Conversion] = Some(TimeAndIntervalConversion)
}

trait ConversionToBinaryCommandEvent {
  self : FixedSize =>
  override def conversion: Option[Conversion] = Some(BinaryCommandEventConversion)
}

trait ConversionToAnalogCommandEvent {
  self : FixedSize =>
  override def conversion: Option[Conversion] = Some(AnalogCommandEventConversion)
}

