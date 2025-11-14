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
import axios from "axios"

export function AddProductionForm({ className, ...props }) {
    
    const [name, setName] = useState("");
    const [description, setDescription] = useState("");
    const [recommendedAge, setRecommendedAge] = useState("");
    const [category, setCategory] = useState("");
    const [condition, setCondition] = useState("");
    const [imageFile, setImageFile] = useState(null); // upload slike

    const handleSubmit = async (e) => {
        e.preventDefault();

         // Ako backend još ne radi → samo pokaži podatke u konzoli
        const toyData = {
            name,
            description,
            recommendedAge,
            category,
            condition,
            imageFile
        };

        console.log("Sending toy:", toyData);

        try {
            //za backend upload
            const formData = new FormData();
            formData.append("name", name);
            formData.append("description", description);
            formData.append("recommendedAge", recommendedAge);
            formData.append("category", category);
            formData.append("condition", condition);
            formData.append("image", imageFile);

            const res = await axios.post("/api/toys", formData, {
                headers: { "Content-Type": "multipart/form-data"}
            });

            console.log("Success:", res.data);
            alert("Igračka dodana!");
        } catch (error) {
            console.error("API error:", error);
            alert("API još ne radi - koristi se mock data.")
        }
    };

    return (
        <form 
            className={cn("space-y-6", className)} 
            onSubmit={handleSubmit} 
            {...props}>
            <FieldGroup>

                {/* Naziv proizvoda */}
                <Field>
                    <FieldLabel>Naziv igračke</FieldLabel>
                    <Input 
                    value={name}
                    onChange={(e) => setName(e.target.value)}
                    placeholder="Npr. LEGO set"
                    required
                    />
                </Field>

                {/* Opis */}
                <Field>
                    <FieldLabel>Opis</FieldLabel>
                    <FieldDescription>
                    Ukratko opiši stanje i izgled igračke.
                    </FieldDescription>
                    <Input 
                    value={description}
                    onChange={(e) => setDescription(e.target.value)}
                    placeholder="Opis igračke..."
                    />
                </Field>

                {/* Dob */}
                <Field>
                    <FieldLabel>Preporučena dob</FieldLabel>
                    <Input 
                        type="number"
                        value={recommendedAge}
                        onChange={(e) => setRecommendedAge(e.target.value)}
                        placeholder="Npr. 6"
                    />
                </Field>

                {/* Kategorija */}
                <Field>
                    <FieldLabel>Kategorija</FieldLabel>
                    <Input 
                        value={category}
                        onChange={(e) => setCategory(e.target.value)}
                        placeholder="Npr. Figura, Vozilo..."
                    />
                </Field>

                {/* Stanje */}
                <Field>
                    <FieldLabel>Stanje</FieldLabel>
                    <Input 
                        value={condition}
                        onChange={(e) => setCondition(e.target.value)}
                        placeholder="Npr. Novo, Rabljeno..."
                    />
                </Field>

                {/* Slika */}
                <Field>
                    <FieldLabel>Slika igračke</FieldLabel>
                    <Input 
                        type="file"
                        accept="image/*"
                        onChange={(e) => setImageFile(e.target.files[0])}
                    />
                </Field>

            </FieldGroup>

            <Button type="submit" className="w-full">
                Dodaj igračku
            </Button>
        </form>
  );
}