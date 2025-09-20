# MPE on Chipyard
This repository contains an updated implementation of the Memory Protection Engine (MPE), originally developed by Omori Yu. The design is based on the concept of Intel SGX-style Integrity Trees and was first introduced in the paper:
"Open-Source Hardware Memory Protection Engine Integrated With NVMM Simulator" [1]
In this work, MPE is integrated into a RISC-V System-on-Chip (SoC) and deployed on an FPGA platform.

## 🔧 Background
The original development was built on the Freedom platform, targeting the VC707 FPGA board. However, Freedom platform is no longer actively maintained or supported for VC707.

## 🚀 Transition to New Platform
To overcome these challenges, this project is transitioning to a new SoC platform and FPGA target. 

## ⏱️ Time
- Simulation time: 1 hour
- Prototyping time: 30 minutes


## Differences from Original Integration
The original implementation of the Memory Protection Engine (MPE) was integrated directly into the Memory Interface Generator (MIG). MIG for VC707 and VCU118 are architecturally similar, with the key difference being the DRAM type—DDR3 for VC707 and DDR4 for VCU118.
This project explores two integration strategies: Peripheral-Based and MIG-Based.

### Peripheral-Based Integration
To enable simulation and modularity, MPE was re-implemented as a peripheral, inspired by Chipyard’s peripheral examples.
Key Points:
1.  Verilator Compatibility
- MIG cannot be simulated in Verilator.
- Peripheral-based design allows full simulation and debugging.
2.  Clock Domain Mismatch
- Rocket Core runs faster than MPE.
- An asynchronous queue was introduced to bridge the speed gap.
- However, this caused timing faults during bitstream generation, so it was deprecated.
- Despite removal, no functional errors were observed in simulation results.
3.  AXI4 Protocol Limitations
- AXI4 frontend/backend communication is not supported in Verilator under Chipyard.
- To resolve this, the handshake logic and FSM states in both frontend and backend were modified to bypass AXI4 constraints.

### MIG-Based Integration
This approach attempts to follow the original design by embedding MPE directly into the MIG path.The original MIG integration modified several files, but:
- Test files are incomplete. 
Some signals are unused, and bitwidths were manually adjusted.
After experimentation, it appears that prot and qos signals for AW and AR channels are required for proper DRAM interaction.
However, how to manipulate these signals within MPE remains unclear.
- There is no reliable method to inspect or debug these signals post-bitstream generation, making validation difficult.
Relevant Files from Original MIG Integration:

Test files: 
chipyard/fpga/fpga-shells/src/main/scala/devices/xilinx/xilinxvcu118mig/test.scala
chipyard/fpga/fpga-shells/src/main/scala/ip/xilinx/vcu118mig/test.scala


## New and Modified Files
This section outlines the key additions and modifications made to support the integration of the Memory Protection Engine (MPE) into the RISC-V SoC environment.

### Build and Top-Level Integration
- **build.sbt**
 Added dependencies and configuration entries to support the MPE module and simulation flow.
- **DigitalTop.scala**
 Integrated MPE as a peripheral in the top-level SoC design.

### Configuration Files
- **mpeRocketConfigs.scala** (src/main/scala/config/)
- **ConfigFragments.scala** (generators/mpe/)

### Peripheral Implementation
- **Periphery.scala** (generators/mpe/)
- **Node.scala** (generators/mpe/)
 Defines the diplomatic node for MPE, enabling clean integration with buses.
 Manages address allocation and connection logic.

### Simulation Frontend/Backend
- **fe_sim.scala** (generators/mpe/)
- **be_sim.scala** (generators/mpe/)


## How to work
The flow is same as Chipyard
- Repository Setup for Chipyard: https://chipyard.readthedocs.io/en/1.8.1/Chipyard-Basics/Initial-Repo-Setup.html
- Simulation on Verilator: https://chipyard.readthedocs.io/en/1.8.1/Simulation/Software-RTL-Simulation.html#
- Prototyping Flow on VCU118: https://chipyard.readthedocs.io/en/1.8.1/Prototyping/VCU118.html


## Next step
When MPE is implemented as a peripheral module, simulation behaves as expected. However, hardware deployment introduces the following challenges:
1. Protocol Migration
- The peripheral interface was migrated from AXI to APB.
- The system uses SRAM instead of DRAM, due to limited memory capacity on the VCU118 board.
- SRAM constraints restrict the size and complexity of test cases.
2. Bitstream Boot Issue
- The FPGA bitstream can boot successfully.
- However, data cannot be written to the assigned address as specified in the instruction stream.
- This discrepancy between simulation and hardware behavior suggests the need for a dedicated driver for MPE.
- The driver should be implemented in the Linux kernel, potentially referencing the NVDLA integration model.


## Reference Links
* MPE on Freedom: https://github.com/uyiromo/freedom/tree/vc707nvmm-mpe
* MPE: https://github.com/uyiromo/OpenMPE/tree/master
* Chipyard 1.8.1: https://chipyard.readthedocs.io/en/1.8.1/index.html
