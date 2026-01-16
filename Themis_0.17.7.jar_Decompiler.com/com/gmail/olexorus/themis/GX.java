package com.gmail.olexorus.themis;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.Map.Entry;

final class Gx extends TypeAdapter<X> {
   static final Type B = (new br()).getType();
   static final Type D = (new AA()).getType();
   static final Type V = (new l5()).getType();
   private final boolean v;
   private final Gson C;
   private static final long a = kt.a(-6027403004119997500L, -1625121859176964693L, MethodHandles.lookup().lookupClass()).a(165615148516514L);

   static TypeAdapter<X> b(C var0, Gson var1) {
      return (new Gx((Boolean)var0.x(ci.i), var1)).nullSafe();
   }

   private Gx(boolean var1, Gson var2) {
      this.v = var1;
      this.C = var2;
   }

   public d<?, ?> W(JsonReader var1) {
      long var2 = a ^ 52818826048223L;
      JsonToken var4 = var1.peek();
      if (var4 != JsonToken.STRING && var4 != JsonToken.NUMBER && var4 != JsonToken.BOOLEAN) {
         if (var4 == JsonToken.BEGIN_ARRAY) {
            GH var32 = null;
            var1.beginArray();

            while(var1.hasNext()) {
               d var33 = this.W(var1);
               if (var32 == null) {
                  var32 = var33.T();
               } else {
                  var32.U(var33);
               }
            }

            if (var32 == null) {
               throw Y(var1.getPath());
            } else {
               var1.endArray();
               return var32.m();
            }
         } else if (var4 != JsonToken.BEGIN_OBJECT) {
            throw Y(var1.getPath());
         } else {
            JsonObject var5 = new JsonObject();
            List var6 = Collections.emptyList();
            String var7 = null;
            String var8 = null;
            String var9 = null;
            List var10 = null;
            String var11 = null;
            String var12 = null;
            String var13 = null;
            String var14 = null;
            String var15 = null;
            String var16 = null;
            boolean var17 = false;
            Ma var18 = null;
            String var19 = null;
            v1 var20 = null;
            d var21 = null;
            v1 var22 = null;
            v1 var23 = null;
            A2 var24 = null;
            boolean var25 = false;
            var1.beginObject();

            while(true) {
               while(true) {
                  while(true) {
                     while(true) {
                        while(true) {
                           while(var1.hasNext()) {
                              String var26 = var1.nextName();
                              if (!var26.equals("text")) {
                                 if (!var26.equals("translate")) {
                                    if (!var26.equals("fallback")) {
                                       if (!var26.equals("with")) {
                                          if (var26.equals("score")) {
                                             var1.beginObject();

                                             while(var1.hasNext()) {
                                                String var35 = var1.nextName();
                                                if (var35.equals("name")) {
                                                   var11 = var1.nextString();
                                                } else if (var35.equals("objective")) {
                                                   var12 = var1.nextString();
                                                } else if (var35.equals("value")) {
                                                   var13 = var1.nextString();
                                                } else {
                                                   var1.skipValue();
                                                }
                                             }

                                             if (var11 == null || var12 == null) {
                                                throw new JsonParseException("A score component requires a name and objective");
                                             }

                                             var1.endObject();
                                          } else if (var26.equals("selector")) {
                                             var14 = var1.nextString();
                                          } else if (var26.equals("keybind")) {
                                             var15 = var1.nextString();
                                          } else if (var26.equals("nbt")) {
                                             var16 = var1.nextString();
                                          } else if (var26.equals("interpret")) {
                                             var17 = var1.nextBoolean();
                                          } else if (var26.equals("block")) {
                                             var18 = (Ma)this.C.fromJson(var1, zK.a);
                                          } else if (var26.equals("entity")) {
                                             var19 = var1.nextString();
                                          } else if (var26.equals("storage")) {
                                             var20 = (v1)this.C.fromJson(var1, zK.s);
                                          } else if (var26.equals("extra")) {
                                             var6 = (List)this.C.fromJson(var1, B);
                                          } else if (var26.equals("separator")) {
                                             var21 = this.W(var1);
                                          } else if (var26.equals("atlas")) {
                                             var22 = (v1)this.C.fromJson(var1, zK.s);
                                          } else if (var26.equals("sprite")) {
                                             var23 = (v1)this.C.fromJson(var1, zK.s);
                                          } else if (Tb.A && var26.equals("player")) {
                                             A2 var27 = (A2)var24;
                                             if (var27 == null) {
                                                var27 = B9.O();
                                             }

                                             var24 = var27;
                                             JsonToken var28 = var1.peek();
                                             if (var28 == JsonToken.STRING) {
                                                var25 = true;
                                                var27.g(var1.nextString());
                                             } else if (var28 != JsonToken.BEGIN_OBJECT) {
                                                var1.skipValue();
                                             } else {
                                                var25 = true;
                                                var1.beginObject();

                                                while(true) {
                                                   while(var1.hasNext()) {
                                                      String var29 = var1.nextName();
                                                      if (var29.equals("name")) {
                                                         var27.g(var1.nextString());
                                                      } else if (var29.equals("id")) {
                                                         var27.Y((UUID)this.C.fromJson(var1, zK.z));
                                                      } else if (!var29.equals("properties")) {
                                                         if (var29.equals("texture")) {
                                                            var27.S((v1)this.C.fromJson(var1, zK.s));
                                                         } else {
                                                            var1.skipValue();
                                                         }
                                                      } else {
                                                         JsonToken var30 = var1.peek();
                                                         if (var30 == JsonToken.BEGIN_ARRAY) {
                                                            var27.F((Collection)this.C.fromJson(var1, V));
                                                         } else if (var30 != JsonToken.BEGIN_OBJECT) {
                                                            var1.skipValue();
                                                         } else {
                                                            var1.beginObject();

                                                            while(var1.hasNext()) {
                                                               String var31 = var1.nextName();
                                                               var1.beginArray();

                                                               while(var1.hasNext()) {
                                                                  var27.p(Rv.S(var31, var1.nextString()));
                                                               }

                                                               var1.endArray();
                                                            }

                                                            var1.endObject();
                                                         }
                                                      }
                                                   }

                                                   var1.endObject();
                                                   break;
                                                }
                                             }
                                          } else if (Tb.A && var26.equals("hat")) {
                                             if (var24 == null) {
                                                var24 = B9.O();
                                             }

                                             ((A2)var24).Q(var1.nextBoolean());
                                          } else {
                                             var5.add(var26, (JsonElement)this.C.fromJson(var1, JsonElement.class));
                                          }
                                       } else if (Tb.Z) {
                                          var10 = (List)this.C.fromJson(var1, D);
                                       } else {
                                          var10 = (List)this.C.fromJson(var1, B);
                                       }
                                    } else {
                                       var9 = var1.nextString();
                                    }
                                 } else {
                                    var8 = var1.nextString();
                                 }
                              } else {
                                 var7 = WU.z(var1);
                              }
                           }

                           Object var34;
                           if (var7 != null) {
                              var34 = X.p().P(var7);
                           } else if (var8 != null) {
                              A4 var36;
                              var34 = var36 = X.Z().m(var8);
                              if (var10 != null) {
                                 if (Tb.Z) {
                                    var36.q(var10);
                                 } else {
                                    var36.h(var10);
                                 }
                              }

                              if (Tb.z) {
                                 var36.R(var9);
                              }
                           } else if (var11 != null && var12 != null) {
                              if (var13 == null) {
                                 var34 = X.l().a(var11).s(var12);
                              } else {
                                 var34 = X.l().a(var11).s(var12).L(var13);
                              }
                           } else if (var14 != null) {
                              var34 = X.t().Q(var14).Z(var21);
                           } else if (var15 != null) {
                              var34 = X.W().a(var15);
                           } else if (var16 != null) {
                              if (var18 != null) {
                                 var34 = ((NO)F(X.L(), var16, var17, var21)).y(var18);
                              } else if (var19 != null) {
                                 var34 = ((vt)F(X.N(), var16, var17, var21)).U(var19);
                              } else {
                                 if (var20 == null) {
                                    throw Y(var1.getPath());
                                 }

                                 var34 = ((vy)F(X.a(), var16, var17, var21)).n(var20);
                              }
                           } else if (var23 != null) {
                              var34 = X.S().u(B9.A(var22 != null ? var22 : wT.Q, var23));
                           } else {
                              if (var24 == null || !var25) {
                                 throw Y(var1.getPath());
                              }

                              var34 = X.S().u(((A2)var24).S());
                           }

                           ((GH)var34).r((WR)this.C.fromJson(var5, zK.g)).y(var6);
                           var1.endObject();
                           return ((GH)var34).m();
                        }
                     }
                  }
               }
            }
         }
      } else {
         return X.N(WU.z(var1));
      }
   }

   private static <C extends mm<C, B>, B extends zB<C, B>> B F(B var0, String var1, boolean var2, X var3) {
      return var0.b(var1).W(var2).G(var3);
   }

   public void d(JsonWriter var1, X var2) {
      long var3 = a ^ 70730722244637L;
      if (var2 instanceof Aa && var2.C().isEmpty() && !var2.R() && this.v) {
         var1.value(((Aa)var2).H());
      } else {
         var1.beginObject();
         if (var2.R()) {
            JsonElement var5 = this.C.toJsonTree(var2.o(), zK.g);
            if (var5.isJsonObject()) {
               Iterator var6 = var5.getAsJsonObject().entrySet().iterator();

               while(var6.hasNext()) {
                  Entry var7 = (Entry)var6.next();
                  var1.name((String)var7.getKey());
                  this.C.toJson((JsonElement)var7.getValue(), var1);
               }
            }
         }

         if (!var2.C().isEmpty()) {
            var1.name("extra");
            this.C.toJson(var2.C(), B, var1);
         }

         if (var2 instanceof Aa) {
            var1.name("text");
            var1.value(((Aa)var2).H());
         } else if (var2 instanceof GY) {
            GY var12 = (GY)var2;
            var1.name("translate");
            var1.value(var12.p());
            if (Tb.z) {
               String var15 = var12.R();
               if (var15 != null) {
                  var1.name("fallback");
                  var1.value(var15);
               }
            }

            boolean var17;
            if (Tb.Z) {
               var17 = !var12.n().isEmpty();
            } else {
               var17 = !var12.p().isEmpty();
            }

            if (var17) {
               var1.name("with");
               if (Tb.Z) {
                  this.C.toJson(var12.n(), D, var1);
               } else {
                  this.C.toJson(var12.p(), B, var1);
               }
            }
         } else if (var2 instanceof uM) {
            uM var13 = (uM)var2;
            var1.name("score");
            var1.beginObject();
            var1.name("name");
            var1.value(var13.e());
            var1.name("objective");
            var1.value(var13.E());
            if (var13.B() != null) {
               var1.name("value");
               var1.value(var13.B());
            }

            var1.endObject();
         } else if (var2 instanceof gO) {
            gO var14 = (gO)var2;
            var1.name("selector");
            var1.value(var14.b());
            this.r(var1, var14.O());
         } else if (var2 instanceof TG) {
            var1.name("keybind");
            var1.value(((TG)var2).T());
         } else if (var2 instanceof mm) {
            mm var16 = (mm)var2;
            var1.name("nbt");
            var1.value(var16.n());
            var1.name("interpret");
            var1.value(var16.o());
            this.r(var1, var16.V());
            if (var2 instanceof rF) {
               var1.name("block");
               this.C.toJson(((rF)var2).Z(), zK.a, var1);
            } else if (var2 instanceof s) {
               var1.name("entity");
               var1.value(((s)var2).w());
            } else {
               if (!(var2 instanceof VA)) {
                  throw U(var2);
               }

               var1.name("storage");
               this.C.toJson(((VA)var2).W(), zK.s, var1);
            }
         } else {
            if (!Tb.A || !(var2 instanceof Vr)) {
               throw U(var2);
            }

            Vr var18 = (Vr)var2;
            B9 var20 = var18.N();
            if (var20 instanceof wT) {
               wT var19 = (wT)var20;
               if (!var19.L().equals(wT.Q)) {
                  var1.name("atlas");
                  this.C.toJson(var19.L(), zK.s, var1);
               }

               var1.name("sprite");
               this.C.toJson(var19.E(), zK.s, var1);
            } else {
               if (!(var20 instanceof Rv)) {
                  throw U(var2);
               }

               Rv var21 = (Rv)var20;
               var1.name("hat");
               var1.value(var21.p());
               String var8 = var21.j();
               UUID var9 = var21.Y();
               List var10 = var21.d();
               v1 var11 = var21.K();
               var1.name("player");
               if (var8 != null && var9 == null && var10.isEmpty() && var11 == null) {
                  var1.value(var8);
               } else {
                  var1.beginObject();
                  if (var8 != null) {
                     var1.name("name");
                     var1.value(var8);
                  }

                  if (var9 != null) {
                     var1.name("id");
                     this.C.toJson(var9, zK.z, var1);
                  }

                  if (!var10.isEmpty()) {
                     var1.name("properties");
                     this.C.toJson(var10, V, var1);
                  }

                  if (var11 != null) {
                     var1.name("texture");
                     this.C.toJson(var11, zK.s, var1);
                  }

                  var1.endObject();
               }
            }
         }

         var1.endObject();
      }
   }

   private void r(JsonWriter var1, X var2) {
      long var3 = a ^ 109622157036567L;
      if (var2 != null) {
         var1.name("separator");
         this.d(var1, var2);
      }

   }

   static JsonParseException Y(Object var0) {
      long var1 = a ^ 3156193107815L;
      return new JsonParseException("Don't know how to turn " + var0 + " into a Component");
   }

   private static IllegalArgumentException U(X var0) {
      long var1 = a ^ 132977572990423L;
      return new IllegalArgumentException("Don't know how to serialize " + var0 + " as a Component");
   }
}
