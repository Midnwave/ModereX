/*     */ package ac.grim.grimac.shaded.incendo.cloud.component;
/*     */ 
/*     */ import ac.grim.grimac.shaded.geantyref.TypeToken;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.CommandManager;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.component.preprocessor.ComponentPreprocessor;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.component.preprocessor.PreprocessorHolder;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandInput;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.description.Describable;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.description.Description;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.key.CloudKey;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.ArgumentParseResult;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.ArgumentParser;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.ParserDescriptor;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.ParserParameters;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.standard.LiteralParser;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.suggestion.SuggestionProvider;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Objects;
/*     */ import org.apiguardian.api.API;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @API(status = API.Status.STABLE)
/*     */ public class CommandComponent<C>
/*     */   implements Comparable<CommandComponent<C>>, PreprocessorHolder<C>, Describable
/*     */ {
/*     */   private final String name;
/*     */   private final ArgumentParser<C, ?> parser;
/*     */   private final Description description;
/*     */   private final ComponentType componentType;
/*     */   private final DefaultValue<C, ?> defaultValue;
/*     */   private final TypeToken<?> valueType;
/*     */   private final SuggestionProvider<C> suggestionProvider;
/*     */   private final Collection<ComponentPreprocessor<C>> componentPreprocessors;
/*     */   
/*     */   public static <C, T> Builder<C, T> builder() {
/*  83 */     return new Builder<>();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <C, T> Builder<C, T> builder(String name, ParserDescriptor<? super C, T> parserDescriptor) {
/* 102 */     return builder().name(name).parser(parserDescriptor);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <C, T> Builder<C, T> builder(CloudKey<T> name, ParserDescriptor<? super C, T> parserDescriptor) {
/* 121 */     return builder().key(name).parser(parserDescriptor);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <C, T> Builder<C, T> ofType(Class<T> clazz, String name) {
/* 140 */     return builder().valueType(clazz).name(name);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   CommandComponent(String name, ArgumentParser<C, ?> parser, TypeToken<?> valueType, Description description, ComponentType componentType, DefaultValue<C, ?> defaultValue, SuggestionProvider<C> suggestionProvider, Collection<ComponentPreprocessor<C>> componentPreprocessors) {
/* 153 */     this.name = name;
/* 154 */     this.parser = parser;
/* 155 */     this.valueType = valueType;
/* 156 */     this.componentType = componentType;
/* 157 */     this.description = description;
/* 158 */     this.defaultValue = defaultValue;
/* 159 */     this.suggestionProvider = suggestionProvider;
/* 160 */     this.componentPreprocessors = new ArrayList<>(componentPreprocessors);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TypeToken<?> valueType() {
/* 169 */     return this.valueType;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ArgumentParser<C, ?> parser() {
/* 178 */     return this.parser;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final String name() {
/* 187 */     return this.name;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final Collection<String> aliases() {
/* 200 */     if (parser() instanceof LiteralParser) {
/* 201 */       return ((LiteralParser)parser()).aliases();
/*     */     }
/* 203 */     return Collections.emptyList();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final Collection<String> alternativeAliases() {
/* 216 */     if (parser() instanceof LiteralParser) {
/* 217 */       return ((LiteralParser)parser()).alternativeAliases();
/*     */     }
/* 219 */     return Collections.emptyList();
/*     */   }
/*     */ 
/*     */   
/*     */   public final Description description() {
/* 224 */     return this.description;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final boolean required() {
/* 235 */     return this.componentType.required();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final boolean optional() {
/* 246 */     return this.componentType.optional();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final ComponentType type() {
/* 255 */     return this.componentType;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DefaultValue<C, ?> defaultValue() {
/* 266 */     return this.defaultValue;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final boolean hasDefaultValue() {
/* 275 */     return (optional() && defaultValue() != null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final SuggestionProvider<C> suggestionProvider() {
/* 284 */     return this.suggestionProvider;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final CommandComponent<C> addPreprocessor(ComponentPreprocessor<C> preprocessor) {
/* 301 */     this.componentPreprocessors.add(Objects.<ComponentPreprocessor<C>>requireNonNull(preprocessor, "preprocessor"));
/* 302 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final ArgumentParseResult<Boolean> preprocess(CommandContext<C> context, CommandInput input) {
/* 319 */     for (ComponentPreprocessor<C> preprocessor : this.componentPreprocessors) {
/* 320 */       ArgumentParseResult<Boolean> result = preprocessor.preprocess(context, input);
/*     */ 
/*     */ 
/*     */       
/* 324 */       if (result.failure().isPresent()) {
/* 325 */         return result;
/*     */       }
/*     */     } 
/* 328 */     return ArgumentParseResult.success(Boolean.valueOf(true));
/*     */   }
/*     */ 
/*     */   
/*     */   public final Collection<ComponentPreprocessor<C>> preprocessors() {
/* 333 */     return Collections.unmodifiableCollection(this.componentPreprocessors);
/*     */   }
/*     */ 
/*     */   
/*     */   public final int hashCode() {
/* 338 */     return Objects.hash(new Object[] { name(), valueType() });
/*     */   }
/*     */ 
/*     */   
/*     */   public final boolean equals(Object o) {
/* 343 */     if (this == o)
/* 344 */       return true; 
/* 345 */     if (o instanceof CommandComponent) {
/* 346 */       CommandComponent<?> that = (CommandComponent)o;
/* 347 */       return (name().equals(that.name()) && valueType().equals(that.valueType()));
/*     */     } 
/* 349 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public final String toString() {
/* 355 */     return String.format("%s{name=%s,type=%s,valueType=%s", new Object[] {
/*     */           
/* 357 */           getClass().getSimpleName(), 
/* 358 */           name(), 
/* 359 */           type(), 
/* 360 */           valueType().getType().getTypeName()
/*     */         });
/*     */   }
/*     */ 
/*     */   
/*     */   public final int compareTo(CommandComponent<C> other) {
/* 366 */     if (type() == ComponentType.LITERAL) {
/* 367 */       if (other.type() == ComponentType.LITERAL) {
/* 368 */         return name().compareTo(other.name());
/*     */       }
/* 370 */       return -1;
/*     */     } 
/* 372 */     if (other.type() == ComponentType.LITERAL) {
/* 373 */       return 1;
/*     */     }
/* 375 */     return 0;
/*     */   }
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE)
/*     */   public static class Builder<C, T>
/*     */   {
/*     */     private CommandManager<C> commandManager;
/*     */     
/*     */     private String name;
/*     */     private ArgumentParser<C, T> parser;
/* 386 */     private Description description = Description.empty();
/*     */     private boolean required = true;
/*     */     private DefaultValue<C, ?> defaultValue;
/*     */     private TypeToken<T> valueType;
/*     */     private SuggestionProvider<C> suggestionProvider;
/* 391 */     private final Collection<ComponentPreprocessor<C>> componentPreprocessors = new ArrayList<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder<C, T> commandManager(CommandManager<C> commandManager) {
/* 406 */       this.commandManager = commandManager;
/* 407 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder<C, T> key(CloudKey<T> cloudKey) {
/* 418 */       return name(cloudKey.name()).valueType(cloudKey.type());
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String name() {
/* 427 */       return this.name;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder<C, T> name(String name) {
/* 439 */       this.name = Objects.<String>requireNonNull(name, "name");
/* 440 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder<C, T> valueType(TypeToken<T> valueType) {
/* 456 */       this.valueType = Objects.<TypeToken<T>>requireNonNull(valueType, "valueType");
/* 457 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder<C, T> valueType(Class<T> valueType) {
/* 473 */       return valueType(TypeToken.get(valueType));
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public ParserDescriptor<C, T> parser() {
/* 482 */       if (this.valueType == null || this.parser == null) {
/* 483 */         return null;
/*     */       }
/* 485 */       return ParserDescriptor.of(this.parser, this.valueType);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder<C, T> parser(ParserDescriptor<? super C, T> parserDescriptor) {
/* 495 */       return parser(parserDescriptor.parser()).valueType(parserDescriptor.valueType());
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public DefaultValue<C, T> defaultValue() {
/* 504 */       if (this.defaultValue == null) {
/* 505 */         return null;
/*     */       }
/* 507 */       return (DefaultValue)this.defaultValue;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder<C, T> defaultValue(DefaultValue<? super C, T> defaultValue) {
/* 519 */       this.defaultValue = (DefaultValue)defaultValue;
/* 520 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder<C, T> required(boolean required) {
/* 532 */       this.required = required;
/* 533 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder<C, T> required() {
/* 542 */       return required(true);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder<C, T> optional() {
/* 551 */       return required(false);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder<C, T> optional(DefaultValue<? super C, T> defaultValue) {
/* 561 */       return optional().defaultValue(defaultValue);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Description description() {
/* 570 */       return this.description;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder<C, T> description(Description description) {
/* 582 */       this.description = Objects.<Description>requireNonNull(description, "description");
/* 583 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public SuggestionProvider<C> suggestionProvider() {
/* 592 */       return this.suggestionProvider;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder<C, T> suggestionProvider(SuggestionProvider<? super C> suggestionProvider) {
/* 604 */       this.suggestionProvider = (SuggestionProvider)suggestionProvider;
/* 605 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder<C, T> preprocessor(ComponentPreprocessor<? super C> preprocessor) {
/* 615 */       this.componentPreprocessors.add((ComponentPreprocessor<C>)Objects.<ComponentPreprocessor<? super C>>requireNonNull(preprocessor, "preprocessor"));
/* 616 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder<C, T> preprocessors(Collection<ComponentPreprocessor<C>> preprocessors) {
/* 626 */       this.componentPreprocessors.addAll(preprocessors);
/* 627 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder<C, T> parser(ArgumentParser<? super C, T> parser) {
/* 640 */       this.parser = (ArgumentParser<C, T>)Objects.<ArgumentParser<? super C, T>>requireNonNull(parser, "parser");
/* 641 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public TypedCommandComponent<C, T> build() {
/*     */       CommandComponent.ComponentType componentType;
/*     */       SuggestionProvider<C> suggestionProvider;
/* 652 */       ArgumentParser<C, T> parser = null;
/* 653 */       if (this.parser != null) {
/* 654 */         parser = this.parser;
/* 655 */       } else if (this.commandManager != null) {
/*     */ 
/*     */         
/* 658 */         parser = this.commandManager.parserRegistry().createParser(this.valueType, ParserParameters.empty()).orElse(null);
/*     */       } 
/* 660 */       if (parser == null) {
/* 661 */         parser = ((ctx, input) -> ArgumentParseResult.failure(new UnsupportedOperationException("No parser was specified")));
/*     */       }
/*     */ 
/*     */ 
/*     */       
/* 666 */       if (this.parser instanceof LiteralParser) {
/* 667 */         componentType = CommandComponent.ComponentType.LITERAL;
/* 668 */       } else if (this.parser instanceof ac.grim.grimac.shaded.incendo.cloud.parser.flag.CommandFlagParser) {
/* 669 */         componentType = CommandComponent.ComponentType.FLAG;
/* 670 */       } else if (this.required) {
/* 671 */         componentType = CommandComponent.ComponentType.REQUIRED_VARIABLE;
/*     */       } else {
/* 673 */         componentType = CommandComponent.ComponentType.OPTIONAL_VARIABLE;
/*     */       } 
/*     */ 
/*     */       
/* 677 */       if (this.suggestionProvider == null) {
/* 678 */         suggestionProvider = parser.suggestionProvider();
/*     */       } else {
/* 680 */         suggestionProvider = this.suggestionProvider;
/*     */       } 
/*     */       
/* 683 */       return new TypedCommandComponent<>(
/* 684 */           Objects.<String>requireNonNull(this.name, "name"), parser, 
/*     */           
/* 686 */           Objects.<TypeToken>requireNonNull(this.valueType, "valueType"), 
/* 687 */           Objects.<Description>requireNonNull(this.description, "description"), componentType, this.defaultValue, suggestionProvider, 
/*     */ 
/*     */ 
/*     */           
/* 691 */           Objects.<Collection<ComponentPreprocessor<C>>>requireNonNull(this.componentPreprocessors, "componentPreprocessors"));
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE)
/*     */   public enum ComponentType
/*     */   {
/* 702 */     LITERAL(true),
/*     */ 
/*     */ 
/*     */     
/* 706 */     REQUIRED_VARIABLE(true),
/*     */ 
/*     */ 
/*     */     
/* 710 */     OPTIONAL_VARIABLE(false),
/*     */ 
/*     */ 
/*     */     
/* 714 */     FLAG(false);
/*     */     
/*     */     private final boolean required;
/*     */     
/*     */     ComponentType(boolean required) {
/* 719 */       this.required = required;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean required() {
/* 730 */       return this.required;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean optional() {
/* 741 */       return !this.required;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\component\CommandComponent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */