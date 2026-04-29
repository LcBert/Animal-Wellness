package com.lucab.animal_wellness.command;

import com.lucab.animal_wellness.attachments.GeneticTraits;
import com.lucab.animal_wellness.attachments.WellnessHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;

public class WellnessCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("wellness")
                .requires(src -> src.hasPermission(4))
                .then(Commands.literal("setAge")
                        .then(Commands.argument("entity", EntityArgument.entity())
                                .then(Commands.literal("baby")
                                        .executes(ctx -> setAge(ctx, 0)))
                                .then(Commands.literal("adult")
                                        .executes(ctx -> setAge(ctx, 1)))
                                .then(Commands.literal("old")
                                        .executes(ctx -> setAge(ctx, 2))
                                )
                        )
                )
                .then(Commands.literal("setGenetic")
                        .then(Commands.argument("entity", EntityArgument.entity())
                                .then(Commands.literal("*")
                                        .then(Commands.argument("value", FloatArgumentType.floatArg(0f, 1f))
                                                .executes(ctx -> setGenetic(ctx, 0))))
                                .then(Commands.literal("productivity")
                                        .then(Commands.argument("value", FloatArgumentType.floatArg(0f, 1f))
                                                .executes(ctx -> setGenetic(ctx, 1))))
                                .then(Commands.literal("efficiency")
                                        .then(Commands.argument("value", FloatArgumentType.floatArg(0f, 1f))
                                                .executes(ctx -> setGenetic(ctx, 2))))
                                .then(Commands.literal("temperament")
                                        .then(Commands.argument("value", FloatArgumentType.floatArg(0f, 1f))
                                                .executes(ctx -> setGenetic(ctx, 3))))
                                .then(Commands.literal("resistance")
                                        .then(Commands.argument("value", FloatArgumentType.floatArg(0f, 1f))
                                                .executes(ctx -> setGenetic(ctx, 4)))
                                )
                        )
                )
        );
    }

    private static int setAge(CommandContext<CommandSourceStack> context, int age) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        Entity entity = EntityArgument.getEntity(context, "entity");
        WellnessHelper helper = WellnessHelper.getInstance(entity);
        if (!helper.isConsideredAnimal()) {
            source.sendFailure(Component.literal("Entity is not an animal"));
            return 0;
        }

        String message = "Set mob age to %s";
        switch (age) {
            case 0 -> {
                helper.setBirth();
                source.sendSuccess(() -> Component.literal(String.format(message, "baby")), false);
            }
            case 1 -> {
                helper.setBirthAsAdult();
                source.sendSuccess(() -> Component.literal(String.format(message, "adult")), false);
            }
            case 2 -> {
                helper.setBirthAsOld();
                source.sendSuccess(() -> Component.literal(String.format(message, "old")), false);
            }
        }

        return 1;
    }

    private static int setGenetic(CommandContext<CommandSourceStack> context, int genetic) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        Entity entity = EntityArgument.getEntity(context, "entity");
        float value = FloatArgumentType.getFloat(context, "value");
        WellnessHelper helper = WellnessHelper.getInstance(entity);
        if (!helper.isConsideredAnimal()) {
            source.sendFailure(Component.literal("Entity is not an animal"));
            return 0;
        }

        String message = "Set mob genetic %s to %.2f%%";
        switch (genetic) {
            case 0 -> {
                for (GeneticTraits.TraitType type : GeneticTraits.TraitType.values()) {
                    helper.getGeneticTraits().setTrait(type, value);
                }
                source.sendSuccess(() -> Component.literal(String.format(message, "all", value * 100)), false);
            }
            case 1 -> {
                helper.getGeneticTraits().setTrait(GeneticTraits.TraitType.PRODUCTIVITY, value);
                source.sendSuccess(() -> Component.literal(String.format(message, "productivity", value * 100)), false);
            }
            case 2 -> {
                helper.getGeneticTraits().setTrait(GeneticTraits.TraitType.EFFICIENCY, value);
                source.sendSuccess(() -> Component.literal(String.format(message, "efficiency", value * 100)), false);
            }
            case 3 -> {
                helper.getGeneticTraits().setTrait(GeneticTraits.TraitType.TEMPERAMENT, value);
                source.sendSuccess(() -> Component.literal(String.format(message, "temperament", value * 100)), false);
            }
            case 4 -> {
                helper.getGeneticTraits().setTrait(GeneticTraits.TraitType.RESISTANCE, value);
                source.sendSuccess(() -> Component.literal(String.format(message, "resistance", value * 100)), false);
            }
        }

        return 1;
    }
}
