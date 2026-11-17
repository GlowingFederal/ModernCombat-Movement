package com.glowingfederal.moderncombatmovement.compat;
/** Native crystal beam and ritual invulnerability components on legacy engines. */
public interface CrystalState {
 void mcm$beam(int x,int y,int z,boolean active);
 boolean mcm$beamActive();int mcm$beamX();int mcm$beamY();int mcm$beamZ();
 void mcm$invulnerable(boolean value);boolean mcm$invulnerable();
}
