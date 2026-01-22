package ac.grim.grimac.shaded.snakeyaml.scanner;

import ac.grim.grimac.shaded.snakeyaml.tokens.Token;

public interface Scanner {
  boolean checkToken(Token.ID... paramVarArgs);
  
  Token peekToken();
  
  Token getToken();
  
  void resetDocumentIndex();
}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\snakeyaml\scanner\Scanner.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */