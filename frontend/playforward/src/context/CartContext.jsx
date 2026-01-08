import { createContext, useContext, useState } from "react";

const CartContext = createContext();

export function CartProvider({ children }) {
  const [cartItems, setCartItems] = useState([]);

  const addToCart = (toy) => {
    setCartItems((prev) => {
      const exists = prev.find(item => item.idIgracka === toy.idIgracka);
      if (exists) return prev; // ne dodaj duplo
      return [...prev, toy];
    });
  };

  const removeFromCart = (id) => {
    setCartItems(prev => prev.filter(item => item.idIgracka !== id));
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
