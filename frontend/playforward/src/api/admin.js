import { api } from "../lib/api";

/**
 * Dohvati sve korisnike (samo za admina)
 */
export async function getAllUsers(limit = 50, offset = 0) {
  try {
    const response = await api(`/admin/korisnici?limit=${limit}&offset=${offset}`);
    return response.data || [];
  } catch (err) {
    console.error("Greška pri dohvaćanju korisnika:", err);
    throw err;
  }
}

/**
 * Dohvati sve donacije (samo za admina)
 */
export async function getAllDonations(limit = 50, offset = 0) {
  try {
    const response = await api(`/admin/donacije?limit=${limit}&offset=${offset}`);
    return response.data || [];
  } catch (err) {
    console.error("Greška pri dohvaćanju donacija:", err);
    throw err;
  }
}

/**
 * Dohvati sve kampanje (samo za admina)
 */
export async function getAllCampaigns(limit = 50, offset = 0) {
  try {
    const response = await api(`/admin/kampanje?limit=${limit}&offset=${offset}`);
    return response.data || [];
  } catch (err) {
    console.error("Greška pri dohvaćanju kampanja:", err);
    throw err;
  }
}

/**
 * Dohvati platformsku statistiku
 */
export async function getPlatformStats() {
  try {
    const response = await api("/admin/statistika");
    return response.data || {};
  } catch (err) {
    console.error("Greška pri dohvaćanju statistike:", err);
    throw err;
  }
}

/**
 * Obriši korisnika (samo za admina)
 */
export async function deleteUser(userId) {
  if (!userId) throw new Error("ID korisnika je obavezan");

  try {
    const response = await api(`/admin/korisnici/${userId}`, {
      method: "DELETE",
    });

    return response.data;
  } catch (err) {
    console.error("Greška pri brisanju korisnika:", err);
    throw err;
  }
}

/**
 * Obriši donaciju (samo za admina)
 */
export async function deleteDonation(donationId) {
  if (!donationId) throw new Error("ID donacije je obavezan");

  try {
    const response = await api(`/admin/donacije/${donationId}`, {
      method: "DELETE",
    });

    return response.data;
  } catch (err) {
    console.error("Greška pri brisanju donacije:", err);
    throw err;
  }
}

/**
 * Obriši kampanju (samo za admina)
 */
export async function deleteCampaign(campaignId) {
  if (!campaignId) throw new Error("ID kampanje je obavezan");

  try {
    const response = await api(`/admin/kampanje/${campaignId}`, {
      method: "DELETE",
    });

    return response.data;
  } catch (err) {
    console.error("Greška pri brisanju kampanje:", err);
    throw err;
  }
}
