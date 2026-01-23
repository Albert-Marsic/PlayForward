import { cn } from "@/lib/utils"
import { Button } from "@/components/ui/button"
import {
  Field,
  FieldGroup,
  FieldLabel,
  FieldDescription
} from "@/components/ui/field"
import { Input } from "@/components/ui/input"
import { useState } from "react"
import { createToy, fileToDataUrl } from "@/api/listing"

export function AddProductionForm({ className, ...props }) {
    
    const [naziv, setNaziv] = useState("");
    const [kategorija, setKategorija] = useState("");
    const [stanje, setStanje] = useState("");
    const [fotografija, setFotografija] = useState("");
    const [uvjeti, setUvjeti] = useState("");
    const [imageFile, setImageFile] = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [success, setSuccess] = useState(false);

    const handleImageChange = async (e) => {
        const file = e.target.files[0];
        if (file) {
            try {
                const dataUrl = await fileToDataUrl(file);
                setFotografija(dataUrl);
                setImageFile(file);
            } catch (err) {
                setError("Greška pri učitavanju slike");
            }
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError(null);
        setSuccess(false);

        if (!naziv || !kategorija || !stanje || !fotografija) {
            setError("Naziv, kategorija, stanje i fotografija su obavezni");
            return;
        }

        try {
            setLoading(true);

            const toyData = {
                naziv,
                kategorija,
                stanje,
                fotografija,
                uvjeti: uvjeti || null
            };

            await createToy(toyData);

            setSuccess(true);
            alert("Igračka je uspješno dodata! 🎉");
            
            // Resetiraj formu
            setNaziv("");
            setKategorija("");
            setStanje("");
            setFotografija("");
            setUvjeti("");
            setImageFile(null);
        } catch (err) {
            setError(err.message || "Greška pri dodavanju igračke");
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    // Dostupne kategorije
    const kategorije = [
        "Plišanci",
        "Puzzle",
        "Društvene igre",
        "LEGO setovi",
        "Vozila",
        "Lutke",
        "Knjige",
        "Glazbene igračke",
        "Sportska oprema",
        "Drugo"
    ];

    // Dostupna stanja
    const stanja = [
        { value: "NOVO", label: "Novo" },
        { value: "KORISTENO", label: "Korišteno" }
    ];

    return (
        <form 
            className={cn("space-y-6", className)} 
            onSubmit={handleSubmit} 
            {...props}>
            <FieldGroup>

                {error && (
                    <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded">
                        {error}
                    </div>
                )}

                {success && (
                    <div className="bg-green-100 border border-green-400 text-green-700 px-4 py-3 rounded">
                        Igračka je uspješno dodana!
                    </div>
                )}

                {/* Naziv igračke */}
                <Field>
                    <FieldLabel>Naziv igračke *</FieldLabel>
                    <Input 
                        value={naziv}
                        onChange={(e) => setNaziv(e.target.value)}
                        placeholder="Npr. LEGO set"
                        required
                    />
                </Field>

                {/* Kategorija */}
                <Field>
                    <FieldLabel>Kategorija *</FieldLabel>
                    <select
                        value={kategorija}
                        onChange={(e) => setKategorija(e.target.value)}
                        required
                        className="border rounded px-3 py-2 w-full"
                    >
                        <option value="">Odaberi kategoriju</option>
                        {kategorije.map(kat => (
                            <option key={kat} value={kat}>{kat}</option>
                        ))}
                    </select>
                </Field>

                {/* Stanje */}
                <Field>
                    <FieldLabel>Stanje *</FieldLabel>
                    <select
                        value={stanje}
                        onChange={(e) => setStanje(e.target.value)}
                        required
                        className="border rounded px-3 py-2 w-full"
                    >
                        <option value="">Odaberi stanje</option>
                        {stanja.map(stan => (
                            <option key={stan.value} value={stan.value}>{stan.label}</option>
                        ))}
                    </select>
                </Field>

                {/* Uvjeti */}
                <Field>
                    <FieldLabel>Uvjeti preuzimanja (opcionalno)</FieldLabel>
                    <FieldDescription>
                        Npr. "Preuzimanje isključivo osobno" ili "Dostava o vlastitom trošku"
                    </FieldDescription>
                    <Input 
                        value={uvjeti}
                        onChange={(e) => setUvjeti(e.target.value)}
                        placeholder="Upiši uvjete..."
                    />
                </Field>

                {/* Slika */}
                <Field>
                    <FieldLabel>Fotografija igračke *</FieldLabel>
                    <FieldDescription>
                        Odaberi najmanje jednu stvarnu fotografiju
                    </FieldDescription>
                    <Input 
                        type="file"
                        accept="image/*"
                        onChange={handleImageChange}
                        required
                    />
                    {fotografija && (
                        <div className="mt-2">
                            <img src={fotografija} alt="Preview" className="w-32 h-32 object-cover rounded" />
                        </div>
                    )}
                </Field>

            </FieldGroup>

            <Button type="submit" className="w-full" disabled={loading}>
                {loading ? "Dodavanje..." : "Dodaj igračku"}
            </Button>
        </form>
  );
}
