import { createContext, useContext, useEffect, useState } from "react";

const CartContext = createContext();

// Helper funkcije za rad s cookiesima (session cookies)
const getCookie = (name) => {
  const value = `; ${document.cookie}`;
  const parts = value.split(`; ${name}=`);
  if (parts.length === 2) {
    return parts.pop().split(';').shift();
  }
  return null;
};

const setCookie = (name, value) => {
  // Session cookie - bez expiration date, briše se kada se browser zatvori
  document.cookie = `${name}=${value}; path=/`;
};

const removeCookie = (name) => {
  document.cookie = `${name}=; expires=Thu, 01 Jan 2026 00:00:00 UTC; path=/;`;
};

export function CartProvider({ children }) {

  // inicijalno stanje čitamo iz cookie-a
  const [cartItems, setCartItems] = useState(() => {
    const storedCart = getCookie("cart");
    return storedCart ? JSON.parse(decodeURIComponent(storedCart)) : [];
  });

  // svaki put kad se košarica promijeni, spremi u cookie
  useEffect(() => {
    if (cartItems.length > 0) {
      setCookie("cart", encodeURIComponent(JSON.stringify(cartItems)));
    } else {
      removeCookie("cart");
    }
  }, [cartItems]);

  const addToCart = (toy) => {
    setCartItems(prev => {
      const toyId = toy.id || toy.idIgracka;
      const exists = prev.find(item => (item.id || item.idIgracka) === toyId);
      if (exists) return prev; // nema duplikata
      return [...prev, toy];
    });
  };

  const removeFromCart = (id) => {
    setCartItems(prev => prev.filter(item => (item.id || item.idIgracka) !== id));
  };

  const clearCart = () => {
    setCartItems([]);
    removeCookie("cart");
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
