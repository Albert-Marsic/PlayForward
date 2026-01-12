import { createContext, useContext, useEffect, useState } from "react";

const CartContext = createContext();

export function CartProvider({ children }) {

  // 1️⃣ inicijalno stanje čitamo iz localStorage
  const [cartItems, setCartItems] = useState(() => {
    const storedCart = localStorage.getItem("cart");
    return storedCart ? JSON.parse(storedCart) : [];
  });

  // 2️⃣ svaki put kad se košarica promijeni → spremi
  useEffect(() => {
    localStorage.setItem("cart", JSON.stringify(cartItems));
  }, [cartItems]);

  const addToCart = (toy) => {
    setCartItems(prev => {
      const exists = prev.find(item => item.idIgracka === toy.idIgracka);
      if (exists) return prev; // nema duplikata
      return [...prev, toy];
    });
  };

  const removeFromCart = (id) => {
    setCartItems(prev => prev.filter(item => item.idIgracka !== id));
  };

  const clearCart = () => {
    setCartItems([]);
    localStorage.removeItem("cart");
  };

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
