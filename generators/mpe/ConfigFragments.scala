// See LICENSE for license details.
package openmpe

import chisel3._

import freechips.rocketchip.config.{Field, Parameters, Config}

/**
 * Config fragment to add a NVDLA to the SoC.
 * Supports "small" and "large" configs only.
 * Can enable synth. RAMs instead of default FPGA RAMs.
 */
class WithMPE() extends Config((site, here, up) => {
  case MPEKey => Some(MPEParams(  address =  0x1000,
  size = 4096,raddress = 0x70000000L))
})
