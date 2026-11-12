package com.glowingfederal.moderncombatmovement.api;

/** Immutable, platform-neutral registration contract. Registration is an explicit opt-in. */
public final class ItemProperties {
    public enum Kind { HAND, SWORD, AXE, TOOL, BOW, CROSSBOW, SPEAR, TRIDENT, MACE, SHIELD, CONSUMABLE }
    public final Kind kind;
    public final double attackSpeed;
    /** Total unenchanted attack damage, including the player's ordinary one-point base. */
    public final double attackDamage;
    public final boolean sweeping;
    public final boolean projectileOptIn;

    public ItemProperties(Kind kind, double attackSpeed, double attackDamage, boolean sweeping, boolean projectileOptIn) {
        if (kind == null || !Double.isFinite(attackSpeed) || attackSpeed <= 0 || attackSpeed > 1024
                || !Double.isFinite(attackDamage) || attackDamage < 0 || attackDamage > 2048) {
            throw new IllegalArgumentException("Invalid combat item properties");
        }
        this.kind = kind;
        this.attackSpeed = attackSpeed;
        this.attackDamage = attackDamage;
        this.sweeping = sweeping;
        this.projectileOptIn = projectileOptIn;
    }
}
