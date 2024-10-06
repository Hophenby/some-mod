package com.taikuus.luomuksia.api.actions;

import com.taikuus.luomuksia.api.entity.proj.AbstractModifiableProj;
import net.minecraft.world.phys.EntityHitResult;

public interface ICritModifier extends IModifier{
    enum CalcType{
        ADD,
        MUL,
        IND_MUL,
        SET
    }

    /**
     * Get the type of calculation this modifier does.
     * Only determines what steps the modifier is applied to the damage calculation in.
     */
    CalcType getCalcType();

    /**
     * modify the value of the damage calculation.
     * @return the modified value.
     */
    double modifyValue(double before, EntityHitResult hitResult, AbstractModifiableProj proj);

}
