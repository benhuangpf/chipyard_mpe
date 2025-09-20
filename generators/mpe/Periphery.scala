// See LICENSE for license details.
package openmpe

import chisel3._
import freechips.rocketchip.config.Field
import freechips.rocketchip.subsystem.BaseSubsystem
import freechips.rocketchip.diplomacy.{LazyModule,BufferParams}
import freechips.rocketchip.tilelink._
import freechips.rocketchip.amba.axi4._

case object MPEKey extends Field[Option[MPEParams]](None)
// case object NVDLAFrontBusExtraBuffers extends Field[Int](0)

trait CanHavePeripheryMPE { this: BaseSubsystem =>
  p(MPEKey).map { params =>
    val mpe = LazyModule(new MPENode(params)(p))

    // fbus.fromMaster(name = Some("nvdla_dbb"), buffer = BufferParams.default) {
    //   TLBuffer.chainNode(p(NVDLAFrontBusExtraBuffers))
    // } := nvdla.dbb_tl_node

    // pbus.toFixedWidthSlave(Some("mpe_data")) { mpe.data_tl_node }
        // val gcd = LazyModule(new GCDAXI4(params, pbus.beatBytes)(p))
    pbus.coupleTo(s"mpe_data") {
        (mpe.data_axi_node
        := AXI4Buffer ()
        := AXI4UserYanker()
        := AXI4Deinterleaver(pbus.blockBytes)
        // := AXI4IdIndexer(params.idBits)
        := TLToAXI4 ()
        := TLWidthWidget(pbus.beatBytes)
        // toVariableWidthSlave doesn't use holdFirstDeny, which TLToAXI4() needsx
        := TLFragmenter(pbus.beatBytes, pbus.blockBytes, holdFirstDeny = true) := _)
    }
    // pbus.coupleTo(s"mpe_data") {
    //     slave.controlXing(NoCrossing)
    //     :*= TLFragmenter(pbus.beatBytes, pbus.blockBytes)
    //     :*= _
    // }
    // pbus.coupleTo("mpe_data") { mpe.data_tl_node := TLFragmenter(pbus.beatBytes, pbus.blockBytes) := _ }


    // ibus.fromSync := nvdla.int_node
  }
}