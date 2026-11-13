package com.glowingfederal.moderncombatmovement.combat;

/** Item.Properties.spear and ToolMaterial data from vanilla 1.21.11, in native item order. */
public enum SpearSpec {
    WOODEN("wooden",59,15,0,.65F,.7F,.75F,5,14,10,15),
    STONE("stone",131,5,1,.75F,.82F,.7F,4.5F,10,9,13.75F),
    COPPER("copper",190,13,1,.85F,.82F,.65F,4,9,8.25F,12.5F),
    IRON("iron",250,14,2,.95F,.95F,.6F,2.5F,8,6.75F,11.25F),
    GOLDEN("golden",32,22,0,.95F,.7F,.7F,3.5F,10,8.5F,13.75F),
    DIAMOND("diamond",1561,10,3,1.05F,1.075F,.5F,3,7.5F,6.5F,10),
    NETHERITE("netherite",2031,15,4,1.15F,1.2F,.4F,2.5F,7,5.5F,8.75F);

    public final String material;
    public final int durability, enchantability, delay, dismountDuration, knockbackDuration, damageDuration;
    public final float materialDamage, swingSeconds, multiplier, dismountSpeed;

    SpearSpec(String material,int durability,int enchantability,float materialDamage,float swingSeconds,
            float multiplier,float delaySeconds,float dismountSeconds,float dismountSpeed,
            float knockbackSeconds,float damageSeconds) {
        this.material=material; this.durability=durability; this.enchantability=enchantability;
        this.materialDamage=materialDamage; this.swingSeconds=swingSeconds; this.multiplier=multiplier;
        delay=(int)(delaySeconds*20); dismountDuration=(int)(dismountSeconds*20);
        knockbackDuration=(int)(knockbackSeconds*20); damageDuration=(int)(damageSeconds*20);
        this.dismountSpeed=dismountSpeed;
    }

    public double attackSpeed() { return 1.0F / swingSeconds; }
    public float jabDamage() { return 1 + materialDamage; }
    public boolean dismount(int elapsed,double speed) { return elapsed>=0 && elapsed<=dismountDuration && speed>=dismountSpeed; }
    public boolean knockback(int elapsed,double speed) { return elapsed>=0 && elapsed<=knockbackDuration && speed>=5.1F; }
    public boolean damage(int elapsed,double relativeSpeed) { return elapsed>=0 && elapsed<=damageDuration && relativeSpeed>=4.6F; }
    public float chargeDamage(double baseAttribute,double relativeSpeed) {
        return (float)baseAttribute + (int)Math.floor(Math.max(0,relativeSpeed)*multiplier);
    }
}
