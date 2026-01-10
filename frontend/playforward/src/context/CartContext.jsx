import { createContext, useContext, useState } from "react";  //createContext = globalni kontenjer, useContext = čita podatke iz kontenjera, useState = lokalno stanje

const CartContext = createContext();

export function CartProvider({ children }) {
  const [cartItems, setCartItems] = useState([]);

  const addToCart = (toy) => {
    setCartItems((prev) => {  //prev = prethodno stanje
      const exists = prev.find(item => item.idIgracka === toy.idIgracka);
      if (exists) return prev; // ne dodaj duplo
      return [...prev, toy];  //dodaj kopiju starog niza s novom igračkom
    });
  };

  const removeFromCart = (id) => {
    setCartItems(prev => prev.filter(item => item.idIgracka !== id)); //filter = zadrži one koje zadovoljavaju uvjet
  };

  const clearCart = () => setCartItems([]);

  return (
    <CartContext.Provider value={{
      cartItems,
      addToCart,
      removeFromCart,
      clearCart
    }}>
      {children}
    </CartContext.Provider>
  );
}

export function useCart() {
  return useContext(CartContext);
}
