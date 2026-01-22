/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketSendEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat.Node;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*    */ import java.util.List;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class WrapperPlayServerDeclareCommands
/*    */   extends PacketWrapper<WrapperPlayServerDeclareCommands>
/*    */ {
/*    */   private List<Node> nodes;
/*    */   private int rootIndex;
/*    */   
/*    */   public WrapperPlayServerDeclareCommands(PacketSendEvent event) {
/* 34 */     super(event);
/*    */   }
/*    */   
/*    */   public WrapperPlayServerDeclareCommands(List<Node> nodes, int rootIndex) {
/* 38 */     super((PacketTypeCommon)PacketType.Play.Server.DECLARE_COMMANDS);
/* 39 */     this.nodes = nodes;
/* 40 */     this.rootIndex = rootIndex;
/*    */   }
/*    */ 
/*    */   
/*    */   public void read() {
/* 45 */     this.nodes = readList(PacketWrapper::readNode);
/* 46 */     this.rootIndex = readVarInt();
/*    */   }
/*    */ 
/*    */   
/*    */   public void write() {
/* 51 */     writeList(this.nodes, PacketWrapper::writeNode);
/* 52 */     writeVarInt(this.rootIndex);
/*    */   }
/*    */   
/*    */   public List<Node> getNodes() {
/* 56 */     return this.nodes;
/*    */   }
/*    */   
/*    */   public void setNodes(List<Node> nodes) {
/* 60 */     this.nodes = nodes;
/*    */   }
/*    */   
/*    */   public int getRootIndex() {
/* 64 */     return this.rootIndex;
/*    */   }
/*    */   
/*    */   public void setRootIndex(int rootIndex) {
/* 68 */     this.rootIndex = rootIndex;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\wrapper\play\server\WrapperPlayServerDeclareCommands.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */